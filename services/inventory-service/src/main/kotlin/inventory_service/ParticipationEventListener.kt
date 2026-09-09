package inventory_service.event

import inventory_service.global.error.BusinessException
import inventory_service.global.error.ErrorCode
import inventory_service.global.reconciliation.DltReconciliationService
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
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val dltReconciliationService: DltReconciliationService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RetryableTopic(
        attempts = "3",
        backOff = BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
        exclude = [JacksonException::class],
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = [ParticipationRequestedEvent.TOPIC], groupId = "inventory-service-group")
    fun onParticipationRequested(event: ParticipationRequestedEvent) {
        try {
            log.info(
                "[inventory-service] 참여 요청 수신: eventId={}, participationId={}, productId={}, userId={}, quantity={}",
                event.eventId, event.participationId, event.productId, event.userId, event.quantity
            )

            inventoryService.decreaseStock(event.productId, event.quantity)
            log.info("[inventory-service] 재고 차감 성공: productId={}, quantity={}", event.productId, event.quantity)

            publishSuccessEvent(event)
        } catch (e: BusinessException) {
            if (shouldRetry(e.errorCode)) {
                throw e
            }

            log.warn("[inventory-service] 재고 차감 비즈니스 실패: {}", e.errorCode.msg)
            publishFailureEvent(event, e.errorCode.name, e.errorCode.msg)
            // 비즈니스 예외(재고 부족 등)는 정상적인 Saga 실패 흐름이므로 throw하지 않고 완료 처리.
        }
    }

    @DltHandler
    fun handleDlt(
        event: ParticipationRequestedEvent,
        @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) originalTopic: String?,
        @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) exceptionMessage: String?
    ) {
        val topic = originalTopic ?: ParticipationRequestedEvent.TOPIC
        val errorMessage = exceptionMessage ?: "FailedEvent: ParticipationRequestedEvent"

        log.error("[DLT] 참여 요청 처리 최종 실패. topic={}, error={}, content={}", topic, errorMessage, event)
        dltReconciliationService.logFailedEvent(topic, event, errorMessage)
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

    private fun shouldRetry(errorCode: ErrorCode): Boolean =
        when (errorCode) {
            ErrorCode.PRODUCT_NOT_FOUND,
            ErrorCode.INTERNAL_ERROR -> true

            else -> false
        }

}
