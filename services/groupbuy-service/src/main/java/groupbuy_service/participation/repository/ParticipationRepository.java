package groupbuy_service.participation.repository;

import groupbuy_service.participation.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, String> {
}
