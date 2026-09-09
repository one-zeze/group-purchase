package inventory_service.global.reconciliation

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface FailedEventRepository : JpaRepository<FailedEvent, String> {
    @Modifying
    @Query(
        value = """
            INSERT INTO tb_failed_event (
                failed_event_id, topic, original_partition, original_offset,
                payload, error_message, created_at
            ) VALUES (
                :failedEventId, :topic, :originalPartition, :originalOffset,
                :payload, :errorMessage, :createdAt
            )
            ON CONFLICT (topic, original_partition, original_offset) DO NOTHING
        """,
        nativeQuery = true
    )
    fun insertIgnoringDuplicate(
        @Param("failedEventId") failedEventId: String,
        @Param("topic") topic: String,
        @Param("originalPartition") originalPartition: Int?,
        @Param("originalOffset") originalOffset: Long?,
        @Param("payload") payload: String,
        @Param("errorMessage") errorMessage: String?,
        @Param("createdAt") createdAt: Instant
    ): Int
}
