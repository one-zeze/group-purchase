package groupbuy_service.groupbuy.repository;

import groupbuy_service.groupbuy.domain.Groupbuy;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupbuyRepository extends JpaRepository<Groupbuy, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Groupbuy g where g.groupbuyId = :groupbuyId")
    Optional<Groupbuy> findByIdWithLock(String groupbuyId);
}
