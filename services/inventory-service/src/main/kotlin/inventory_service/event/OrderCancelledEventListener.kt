package inventory_service.event

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
class OrderCancelledEventListener(
    private val inventoryService: InventoryService
) {
    private val log = KotlinLogging.logger {}

    @RetryableTopic(
        attempts = "3",
        backOff = BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
        exclude = [JacksonException::class],
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = ["order.cancelled"], groupId = "inventory-service-order-cancelled")
    fun restoreStock(event: OrderCancelledEvent) {
        try {
            inventoryService.increaseStock(event.productId, event.quantity)
        } catch (e: Exception) {
            log.error(e) { "주문취소 재고 처리 실패" }
            throw e
        }
    }

    @DltHandler
    fun handleDlt(event: OrderCancelledEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        log.error { "[DLT] 주문취소 재고 처리 최종 실패. topic: $topic, content: $event" }
    }
}
