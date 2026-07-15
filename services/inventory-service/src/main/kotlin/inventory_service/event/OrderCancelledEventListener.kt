package inventory_service.event

import inventory_service.service.InventoryService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class OrderCancelledEventListener (
    private val jsonMapper: JsonMapper,
    private val inventoryService: InventoryService
){
    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = ["order.cancelled"], groupId =  "inventory-service-order-cancelled")
    fun restoreStock(message: String) {

        try {
            val event = jsonMapper.readValue(message, OrderCancelledEvent::class.java)
            inventoryService.increaseStock(event.productId, event.quantity);
        }catch (e: Exception){
            log.error(e) { "주문취소 재고 처리 실패" }
            throw e
        }
    }


}