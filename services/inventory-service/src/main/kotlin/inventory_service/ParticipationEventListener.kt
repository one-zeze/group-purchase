package inventory_service

import tools.jackson.databind.ObjectMapper
import inventory_service.event.ParticipationRequestedEvent
import inventory_service.event.StockDecreasedEvent
import inventory_service.event.StockDecreaseFailedEvent
import inventory_service.global.error.BusinessException
import inventory_service.service.InventoryService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ParticipationEventListener(
    private val objectMapper: ObjectMapper,
    private val inventoryService: InventoryService,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["groupbuy.participation.requested"], groupId = "inventory-service-group")
    fun onParticipationRequested(message: String) {
        val event = objectMapper.readValue(message, ParticipationRequestedEvent::class.java)
        try {
            log.info(
                "[inventory-service] 참여 요청 수신: eventId={}, participationId={}, productId={}, userId={}, quantity={}",
                event.eventId, event.participationId, event.productId, event.userId, event.quantity
            )

            inventoryService.decreaseStock(event.productId, event.quantity)
            log.info("[inventory-service] 재고 차감 성공: productId={}, quantity={}", event.productId, event.quantity)
            
            publishSuccessEvent(event)
            
        } catch (e: BusinessException) {
            log.error("[inventory-service] 재고 차감 비즈니스 실패: {}", e.errorCode.msg)
            publishFailureEvent(event, e.errorCode.name, e.errorCode.msg)
        } catch (e: Exception) {
            log.error("[inventory-service] 재고 차감 시스템 오류: {}", e.message)
            publishFailureEvent(event, "INTERNAL_ERROR", e.message ?: "Unknown error")
        }
    }

    private fun publishSuccessEvent(requestEvent: ParticipationRequestedEvent) {
        val successEvent = StockDecreasedEvent(
            participationId = requestEvent.participationId,
            productId = requestEvent.productId,
            quantity = requestEvent.quantity
        )
        val payload = objectMapper.writeValueAsString(successEvent)
        kafkaTemplate.send(StockDecreasedEvent.TOPIC, requestEvent.participationId, payload)
    }

    private fun publishFailureEvent(requestEvent: ParticipationRequestedEvent, errorCode: String, errorMessage: String) {
        val failureEvent = StockDecreaseFailedEvent(
            participationId = requestEvent.participationId,
            productId = requestEvent.productId,
            quantity = requestEvent.quantity,
            errorCode = errorCode,
            errorMessage = errorMessage
        )
        val payload = objectMapper.writeValueAsString(failureEvent)
        kafkaTemplate.send(StockDecreaseFailedEvent.TOPIC, requestEvent.participationId, payload)
    }
}
