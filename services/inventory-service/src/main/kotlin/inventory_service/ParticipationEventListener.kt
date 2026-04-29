package inventory_service

import tools.jackson.databind.ObjectMapper
import inventory_service.event.ParticipationRequestedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ParticipationEventListener(
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["groupbuy.participation.requested"], groupId = "inventory-service-group")
    fun onParticipationRequested(message: String) {
        val event = objectMapper.readValue(message, ParticipationRequestedEvent::class.java)
        log.info(
            "[inventory-service] 참여 요청 수신: eventId={}, participationId={}, productId={}, userId={}, quantity={}",
            event.eventId, event.participationId, event.productId, event.userId, event.quantity
        )
    }
}
