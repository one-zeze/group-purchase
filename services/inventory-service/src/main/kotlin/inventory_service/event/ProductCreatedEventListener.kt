package inventory_service.event

import inventory_service.global.error.BusinessException
import inventory_service.global.error.ErrorCode
import inventory_service.global.reconciliation.DltReconciliationService
import inventory_service.service.InventoryService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.BackOff
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import tools.jackson.core.JacksonException

@Component
class ProductCreatedEventListener(
    private val inventoryService: InventoryService,
    private val dltReconciliationService: DltReconciliationService
) {
    private val log = KotlinLogging.logger {}

    @RetryableTopic(
        attempts = "3",
        backOff = BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
        exclude = [JacksonException::class],
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = [ProductCreatedEvent.TOPIC], groupId = "product-created-event")
    fun registProduct(event: ProductCreatedEvent) {
        try {
            log.info { "상품등록 이벤트 메시지 수신 성공: $event" }
            inventoryService.registStock(event.productId, event.initialStock)
        } catch (e: BusinessException) {
            when (e.errorCode) {
                ErrorCode.STOCK_ALREADY_EXIST -> {
                    log.info { "이미 등록된 재고입니다. productId: ${e.message}" }
                    return
                }
                else -> {
                    throw e
                }
            }
        }
    }

    @DltHandler
    fun handleDlt(
        event: ProductCreatedEvent,
        @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) originalTopic: String?,
        @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) exceptionMessage: String?
    ) {
        val topic = originalTopic ?: ProductCreatedEvent.TOPIC
        val errorMessage = exceptionMessage ?: "FailedEvent: ProductCreatedEvent"

        log.error { "[DLT] 상품 등록 처리 최종 실패. topic=$topic, error=$errorMessage, content=$event" }
        dltReconciliationService.logFailedEvent(topic, event, errorMessage)
    }
}
