package inventory_service.event

import inventory_service.global.error.BusinessException
import inventory_service.global.error.ErrorCode
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
import tools.jackson.databind.json.JsonMapper

@Component
class ProductCreatedEventListener(
    private val jsonMapper: JsonMapper,
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
    @KafkaListener(topics = ["product.created"], groupId = "product-created-event")
    fun registProduct(message: String) {
        try {
            val event = jsonMapper.readValue(message, ProductCreatedEvent::class.java)
            log.info { "상품등록 이벤트 메시지 수신 성공: $event" }
            inventoryService.registStock(event.productId, event.initialStock)
        } catch (e: BusinessException) {
            when (e.errorCode) {
                ErrorCode.STOCK_ALREADY_EXIST -> {
                    log.info { "이미 등록된 재고입니다. productId: ${e.message}" }
                    return
                }
                else -> {
                    log.error(e) { "상품등록 이벤트 처리 실패" }
                    throw e
                }
            }
        } catch (e: Exception) {
            log.error(e) { "상품등록 이벤트 메시지 수신 실패" }
            throw e
        }
    }

    @DltHandler
    fun handleDlt(message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        log.error { "[DLT] 상품 등록 처리 최종 실패. topic: $topic, content: $message" }
    }
}
