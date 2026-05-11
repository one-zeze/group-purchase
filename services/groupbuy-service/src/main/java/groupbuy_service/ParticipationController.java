package groupbuy_service;

import tools.jackson.databind.ObjectMapper;
import groupbuy_service.participation.event.ParticipationRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groupbuy")
public class ParticipationController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ParticipationController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{groupbuyId}/participate")
    public String participate(
            @PathVariable String groupbuyId,
            @RequestParam String productId,
            @RequestParam(defaultValue = "anonymous") String userId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        try {
            ParticipationRequestedEvent event = ParticipationRequestedEvent.of(
                    UUID.randomUUID().toString(), groupbuyId, productId, userId, quantity
            );
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(ParticipationRequestedEvent.TOPIC, productId, payload);
            return "이벤트 발행 완료: " + event.eventId();
        } catch (Exception e) {
            throw new RuntimeException("이벤트 직렬화 실패", e);
        }
    }
}
