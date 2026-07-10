package groupbuy_service.participation.event;

import groupbuy_service.order.event.OrderCancelledEvent;
import groupbuy_service.order.event.OrderCreatedEvent;
import groupbuy_service.participation.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledEventListener {

    private final ParticipationService participationService;
    private final JsonMapper jsonMapper;

    @KafkaListener(topics = OrderCancelledEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onOrderCancelledEvent(String message) {

        try{
            OrderCancelledEvent event = jsonMapper.readValue(message, OrderCancelledEvent.class);
            participationService.failParticipation(event.participationId());
        }catch (Exception e){
            log.error("[groupbuy-service]주문 취소 이벤트 처리 실패: {}", message, e);
            throw e;
        }

    }

}
