package groupbuy_service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groupbuy")
public class ParticipationController {

    private static final String TOPIC = "groupbuy.participation.requested";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ParticipationController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/{productId}/participate")
    public String participate(@PathVariable String productId) {
        String message = "{\"productId\":\"" + productId + "\",\"quantity\":1}";
        kafkaTemplate.send(TOPIC, productId, message);
        return "이벤트 발행 완료: " + message;
    }
}
