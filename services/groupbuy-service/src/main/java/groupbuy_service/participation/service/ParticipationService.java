package groupbuy_service.participation.service;

import groupbuy_service.participation.domain.ParticipationRequest;

public interface ParticipationService {

    void participate(String groupbuyId, ParticipationRequest request);

    void confirmParticipation(String participationId);

    void failParticipation(String participationId);

}
