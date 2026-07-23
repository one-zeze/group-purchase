package groupbuy_service.participation.event;

import groupbuy_service.global.reconciliation.service.DltReconciliationService;
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
public class StockDecreasedEventListener {

    private final ParticipationService participationService;
    private final DltReconciliationService dltReconciliationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
            exclude = {JacksonException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = StockDecreasedEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onStockDecreased(StockDecreasedEvent event) throws Exception {
        try {
            log.info("[groupbuy-service] 재고 차감 성공 수신: participationId={}", event.participationId());
            participationService.confirmParticipation(event.participationId());
        } catch (Exception e) {
            log.error("재고 차감 성공 이벤트 처리 중 오류", e);
            throw e;
        }
    }

    @DltHandler
    public void handleDlt(StockDecreasedEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("[DLT] 재고 차감 성공 이벤트 처리 최종 실패. topic: {}, content: {}", topic, event);
        dltReconciliationService.logFailedEvent(topic, event, "FailedEvent: StockDecreasedEvent");
    }
}
