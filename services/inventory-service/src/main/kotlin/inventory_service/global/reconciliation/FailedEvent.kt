package inventory_service.global.reconciliation

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "tb_failed_event",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_failed_event_original_record",
            columnNames = ["topic", "original_partition", "original_offset"]
        )
    ]
)
class FailedEvent(
    @Id
    @Column(name = "failed_event_id", length = 50)
    val failedEventId: String = UUID.randomUUID().toString(),

    @Column(nullable = false, length = 100)
    val topic: String,

    @Column(name = "original_partition")
    val originalPartition: Int?,

    @Column(name = "original_offset")
    val originalOffset: Long?,

    @Column(nullable = false, columnDefinition = "TEXT")
    val payload: String,

    @Column(name = "error_message", columnDefinition = "TEXT")
    val errorMessage: String?,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
) {
    protected constructor() : this(
        topic = "",
        originalPartition = null,
        originalOffset = null,
        payload = "",
        errorMessage = null
    )
}
