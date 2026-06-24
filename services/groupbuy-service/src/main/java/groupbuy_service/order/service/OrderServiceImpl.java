package groupbuy_service.order.service;

import groupbuy_service.global.BusinessException;
import groupbuy_service.global.ErrorCode;
import groupbuy_service.groupbuy.repository.GroupbuyRepository;
import groupbuy_service.order.domain.Order;
import groupbuy_service.order.repository.OrderRepository;
import groupbuy_service.participation.domain.Participation;
import groupbuy_service.participation.domain.ParticipationStatus;
import groupbuy_service.participation.repository.ParticipationRepository;
import groupbuy_service.product.domain.Product;
import groupbuy_service.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ParticipationRepository participationRepository;
    private final GroupbuyRepository groupbuyRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void createOrdersForGroupbuy(String groupbuyId, String productId) {

        List<Participation> participations = participationRepository.findAllByGroupbuyIdAndStatus(groupbuyId, ParticipationStatus.SUCCESS);
        if (participations.isEmpty()){
            log.info("공동구매 참여자가 없습니다. groupbuyId: {}", groupbuyId);
            return;
        }

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        List<Order> orders = participations.stream().map(
                p -> {
                    BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(p.getQuantity()));
                    return Order.builder()
                            .groupbuyId(groupbuyId)
                            .productId(productId)
                            .userId(p.getUserId())
                            .participationId(p.getParticipationId())
                            .quantity(p.getQuantity())
                            .totalPrice(totalPrice)
                            .build();
                }).toList();

        orderRepository.saveAll(orders);
        log.info("공동구매 주문 일괄 생성 groupbuyId: {}, order: {}건", groupbuyId, orders.size());
    }
}
