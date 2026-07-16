package inventory_service.event

import inventory_service.global.error.BusinessException
import inventory_service.service.InventoryService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.BackOff
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import tools.jackson.core.JacksonException
import tools.jackson.databind.json.JsonMapper

@Component
class ParticipationEventListener(
    private val jsonMapper: JsonMapper,
    private val inventoryService: InventoryService,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RetryableTopic(
        attempts = "3",
        backOff = BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
        exclude = [JacksonException::class],
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = ["groupbuy.participation.requested"], groupId = "inventory-service-group")
    fun onParticipationRequested(message: String) {
        val event = jsonMapper.readValue(message, ParticipationRequestedEvent::class.java)
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
            // 비즈니스 예외(재고 부족 등)는 정상적인 Saga 실패 흐름이므로 throw하지 않고 완료 처리.
        } catch (e: Exception) {
            log.error("[inventory-service] 재고 차감 시스템 오류: {}", e.message)
            publishFailureEvent(event, "INTERNAL_ERROR", e.message ?: "Unknown error")
            throw e // 시스템 예외(DB 다운 등)는 throw하여 재시도를 유발.
        }
    }

    @DltHandler
    fun handleDlt(message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        log.error("[DLT] 참여 요청 처리 최종 실패. topic: $topic, content: $message")
    }

    private fun publishSuccessEvent(requestEvent: ParticipationRequestedEvent) {
        val successEvent = StockDecreasedEvent(
            participationId = requestEvent.participationId,
            productId = requestEvent.productId,
            quantity = requestEvent.quantity
        )
        val payload = jsonMapper.writeValueAsString(successEvent)
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
        val payload = jsonMapper.writeValueAsString(failureEvent)
        kafkaTemplate.send(StockDecreaseFailedEvent.TOPIC, requestEvent.participationId, payload)
    }
}
