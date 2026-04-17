package inventory_service

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ParticipationEventListener {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["groupbuy.participation.requested"], groupId = "inventory-service-group")
    fun onParticipationRequested(message: String) {
        log.info("[inventory-service] 이벤트 수신: {}", message)
    }
}
