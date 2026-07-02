package groupbuy_service.payment.service;

import groupbuy_service.order.event.OrderCreatedEvent;
import groupbuy_service.payment.domain.Payment;
import groupbuy_service.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void createPayments(OrderCreatedEvent event) {
        log.info("결제정보 생성: groupbuyId={}, ordersCount={}", event.groupbuyId(), event.orders().size());

        List<Payment> payments = event.orders().stream()
                .map(orderInfo -> Payment.builder()
                        .orderId(orderInfo.orderId())
                        .userId(orderInfo.userId())
                        .amount(orderInfo.totalPrice())
                        .build()).toList();

        paymentRepository.saveAll(payments);
    }




}
