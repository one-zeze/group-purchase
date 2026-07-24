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
public class StockDecreaseFailedEventListener {

    private final ParticipationService participationService;
    private final DltReconciliationService dltReconciliationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
            exclude = {JacksonException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = StockDecreaseFailedEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onStockDecreaseFailed(StockDecreaseFailedEvent event) throws Exception {
        log.info("[groupbuy-service] 재고 차감 실패 수신: participationId={}, reason={}",
                event.participationId(), event.errorMessage());
        participationService.failParticipation(event.participationId());
    }

    @DltHandler
    public void handleDlt(
            StockDecreaseFailedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage
    ) {
        String errorMessage = exceptionMessage != null
                ? exceptionMessage
                : "FailedEvent: StockDecreaseFailedEvent";

        log.error("[DLT] 재고 차감 실패 이벤트 처리 최종 실패. topic: {}, event: {}, content: {}", topic, event, errorMessage);
        dltReconciliationService.logFailedEvent(topic, event, errorMessage);

    }
}
