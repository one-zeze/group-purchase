package inventory_service.event

import tools.jackson.databind.ObjectMapper
import inventory_service.service.InventoryService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProductCreatedEventListener(
    private val objectMapper: ObjectMapper,
    private val inventoryService: InventoryService
) {

    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = ["product.created"], groupId = "product-created-event")
    fun registProduct(message: String) {
        try {
            val event = objectMapper.readValue(message, ProductCreatedEvent::class.java)
            log.info { "상품등록 이벤트 메시지 수신 성공: $event" }
            inventoryService.registStock(event.productId, event.initialStock)
        }
        catch (e: Exception){
            log.error(e) {"상품등록 이벤트 메시지 수신 실패"}
        }
    }

}