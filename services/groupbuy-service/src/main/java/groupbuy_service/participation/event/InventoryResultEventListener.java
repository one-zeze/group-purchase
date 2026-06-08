package groupbuy_service.participation.event;

import groupbuy_service.participation.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryResultEventListener {

    private final ParticipationService participationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = StockDecreasedEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onStockDecreased(String message) {
        try {
            StockDecreasedEvent event = objectMapper.readValue(message, StockDecreasedEvent.class);
            log.info("[groupbuy-service] 재고 차감 성공 수신: participationId={}", event.participationId());
            participationService.confirmParticipation(event.participationId());
        } catch (Exception e) {
            log.error("재고 차감 성공 이벤트 처리 중 오류", e);
        }
    }

    @KafkaListener(topics = StockDecreaseFailedEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onStockDecreaseFailed(String message) {
        try {
            StockDecreaseFailedEvent event = objectMapper.readValue(message, StockDecreaseFailedEvent.class);
            log.info("[groupbuy-service] 재고 차감 실패 수신: participationId={}, reason={}", 
                    event.participationId(), event.errorMessage());
            participationService.failParticipation(event.participationId());
        } catch (Exception e) {
            log.error("재고 차감 실패 이벤트 처리 중 오류", e);
        }
    }
}
