package inventory_service.event

import java.time.Instant

data class OrderCancelledEvent(
    val eventId: String,
    val version: Int,
    val occurredAt: Instant,
    val participationId: String,
    val orderId: String,
    val productId: String,
    val quantity: Int = 0
)
