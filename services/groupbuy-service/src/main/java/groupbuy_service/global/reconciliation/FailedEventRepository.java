package groupbuy_service.global.reconciliation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedEventRepository extends JpaRepository<FailedEvent, String> {
}
