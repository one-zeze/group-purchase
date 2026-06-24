package groupbuy_service.order.event;

import groupbuy_service.groupbuy.event.GroupbuyConfirmedEvent;
import groupbuy_service.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupbuyConfirmedEventListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = GroupbuyConfirmedEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onGroupbuyCOnfirmedEvent(String message) {
        try{
            GroupbuyConfirmedEvent event = objectMapper.readValue(message, GroupbuyConfirmedEvent.class);
            orderService.createOrdersForGroupbuy(event.groupbuyId(), event.productId());
        }
        catch (Exception e){
            log.error("공동구매 확정 이벤트 발행 실패: ()", e);
        }

    }

}
