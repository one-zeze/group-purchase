package groupbuy_service.participation.event;

import groupbuy_service.global.reconciliation.service.DltReconciliationService;
import groupbuy_service.order.event.OrderCancelledEvent;
import groupbuy_service.participation.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledEventListener {

    private final ParticipationService participationService;
    private final DltReconciliationService dltReconciliationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
            exclude = {JacksonException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = OrderCancelledEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onOrderCancelledEvent(OrderCancelledEvent event) throws Exception {
        try{
            participationService.failParticipation(event.participationId());
        }catch (Exception e){
            log.error("[groupbuy-service]주문 취소 이벤트 처리 실패: {}", event, e);
            throw e;
        }
    }

    @DltHandler
    public void handleDlt(OrderCancelledEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("🚨 [DLT] 주문 취소 처리 최종 실패. topic: {}, content: {}", topic, event);
        dltReconciliationService.logFailedEvent(topic, event, "FailedEvent: OrderCancelledEvent");
    }
}
