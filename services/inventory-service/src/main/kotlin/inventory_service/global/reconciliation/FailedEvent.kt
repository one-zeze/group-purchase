package inventory_service.global.reconciliation

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "tb_failed_event")
class FailedEvent(
    @Id
    @Column(name = "failed_event_id", length = 50)
    val failedEventId: String = UUID.randomUUID().toString(),

    @Column(nullable = false, length = 100)
    val topic: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val payload: String,

    @Column(name = "error_message", columnDefinition = "TEXT")
    val errorMessage: String?,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
) {
    protected constructor() : this(
        topic = "",
        payload = "",
        errorMessage = null
    )
}
