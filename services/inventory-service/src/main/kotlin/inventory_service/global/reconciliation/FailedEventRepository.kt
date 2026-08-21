package inventory_service.global.reconciliation

import org.springframework.data.jpa.repository.JpaRepository

interface FailedEventRepository : JpaRepository<FailedEvent, String> {
}