package groupbuy_service.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventBridge {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDomainEvent(DomainEvent event) {
        log.info("Publishing event to Kafka after commit: {}", event.getTopic());
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(event.getTopic(), event.getKey(), payload);
        } catch (Exception e) {
            log.error("Failed to serialize or send event: {}", event.getTopic(), e);
        }
    }
}
