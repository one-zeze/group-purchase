package inventory_service.event

import java.time.Instant
import java.util.UUID

data class StockDecreasedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val occurredAt: Instant = Instant.now(),
    val participationId: String,
    val productId: String,
    val quantity: Int
) {
    companion object {
        const val TOPIC = "inventory.stock.decreased"
    }
}

data class StockDecreaseFailedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val occurredAt: Instant = Instant.now(),
    val participationId: String,
    val productId: String,
    val quantity: Int,
    val errorCode: String,
    val errorMessage: String
) {
    companion object {
        const val TOPIC = "inventory.stock.decrease-failed"
    }
}
