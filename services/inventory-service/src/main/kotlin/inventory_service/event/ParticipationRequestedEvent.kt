package inventory_service.event

import java.time.Instant

data class ParticipationRequestedEvent(
    val eventId: String = "",
    val version: Int = 1,
    val occurredAt: Instant = Instant.now(),
    val participationId: String = "",
    val groupbuyId: String = "",
    val productId: String = "",
    val userId: String = "",
    val quantity: Int = 0
)
