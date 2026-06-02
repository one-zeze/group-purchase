package groupbuy_service.groupbuy.repository;

import groupbuy_service.groupbuy.domain.Groupbuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupbuyRepository extends JpaRepository<Groupbuy, String> {
}
