package groupbuy_service.participation.service;

import groupbuy_service.global.BusinessException;
import groupbuy_service.global.ErrorCode;
import groupbuy_service.participation.domain.Participation;
import groupbuy_service.participation.domain.ParticipationRequest;
import groupbuy_service.participation.domain.ParticipationStatus;
import groupbuy_service.participation.event.ParticipationRequestedEvent;
import groupbuy_service.participation.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public void participate(String groupbuyId, ParticipationRequest request) {
        Participation participation = Participation.builder()
                .groupbuyId(groupbuyId)
                .productId(request.productId())
                .userId(request.userId())
                .quantity(request.quantity())
                .build();
        
        Participation savedParticipation = participationRepository.save(participation);

        try {
            ParticipationRequestedEvent event = ParticipationRequestedEvent.from(savedParticipation);
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(ParticipationRequestedEvent.TOPIC, event.productId(), payload);
            log.info("참여 요청 이벤트 발행 성공: participationId={}", savedParticipation.getParticipationId());
        } catch (Exception e) {
            log.error("이벤트 발행 중 오류 발생", e);
            throw new RuntimeException("이벤트 발행 실패", e);
        }
    }

    @Transactional
    @Override
    public void confirmParticipation(String participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR));
        
        participation.updateStatus(ParticipationStatus.SUCCESS);
        log.info("참여 확정 성공: participationId={}", participationId);
    }

    @Transactional
    @Override
    public void failParticipation(String participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR));
        
        participation.updateStatus(ParticipationStatus.FAILED);
        log.info("참여 실패 처리 완료: participationId={}", participationId);
    }
}
