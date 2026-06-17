package inventory_service.event

import java.math.BigDecimal
import java.time.Instant

data class ProductCreatedEvent(
    val eventId: String,
    val version: Int,
    val occurredAt: Instant,
    val productId: String,
    val name: String,
    val price: BigDecimal,
    val initialStock: Int
)
