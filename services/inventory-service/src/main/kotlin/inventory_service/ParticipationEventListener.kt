package inventory_service

import tools.jackson.databind.ObjectMapper
import inventory_service.event.ParticipationRequestedEvent
import inventory_service.service.InventoryService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ParticipationEventListener(
    private val objectMapper: ObjectMapper,
    private val inventoryService: InventoryService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["groupbuy.participation.requested"], groupId = "inventory-service-group")
    fun onParticipationRequested(message: String) {
        try {
            val event = objectMapper.readValue(message, ParticipationRequestedEvent::class.java)
            log.info(
                "[inventory-service] 참여 요청 수신: eventId={}, participationId={}, productId={}, userId={}, quantity={}",
                event.eventId, event.participationId, event.productId, event.userId, event.quantity
            )

            inventoryService.decreaseStock(event.productId, event.quantity)
            log.info("[inventory-service] 재고 차감 성공: productId={}, quantity={}", event.productId, event.quantity)
            
        } catch (e: Exception) {
            log.error("[inventory-service] 재고 차감 실패: {}", e.message)
            // TODO: 실패 시 보상 트랜잭션 또는 에러 이벤트 발행 필요
        }
    }
}
