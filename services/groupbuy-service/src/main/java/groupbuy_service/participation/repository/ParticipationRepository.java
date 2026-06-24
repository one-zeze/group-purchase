package groupbuy_service.participation.repository;

import groupbuy_service.participation.domain.Participation;
import groupbuy_service.participation.domain.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, String> {
    List<Participation> findAllByGroupbuyIdAndStatus(String groupbuyId, ParticipationStatus status);
}
