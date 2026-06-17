package groupbuy_service.participation.service;

import groupbuy_service.global.BusinessException;
import groupbuy_service.global.ErrorCode;
import groupbuy_service.groupbuy.domain.Groupbuy;
import groupbuy_service.groupbuy.domain.GroupbuyStatus;
import groupbuy_service.groupbuy.event.GroupbuyConfirmedEvent;
import groupbuy_service.groupbuy.repository.GroupbuyRepository;
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

    private final GroupbuyRepository groupbuyRepository;
    private final ParticipationRepository participationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public void participate(String groupbuyId, ParticipationRequest request) {
        Groupbuy groupbuy = groupbuyRepository.findByIdWithLock(groupbuyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR));

        if (groupbuy.getStatus() != GroupbuyStatus.OPEN) {
            throw new BusinessException(ErrorCode.PARTICIPATION_NOT_AVAILABLE);
        }

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

        if (participation.getStatus() != ParticipationStatus.REQUESTED) {
            log.info("이미 처리된 참여 요청입니다. participationId={}, status={}", participationId, participation.getStatus());
            return;
        }

        Groupbuy groupbuy = groupbuyRepository.findByIdWithLock(participation.getGroupbuyId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUPBUY_NOT_FOUND));

        // OPEN 상태일 때만 수량 추가
        if (groupbuy.getStatus() != GroupbuyStatus.OPEN) {
            log                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        .warn("공동구매가 오픈 상태가 아니므로 참여를 확정할 수 없습니다. groupbuyId={}, status={}",
                    groupbuy.getGroupbuyId(), groupbuy.getStatus());
            participation.updateStatus(ParticipationStatus.FAILED);
            return;
        }

        groupbuy.addCurrentQuantity(participation.getQuantity());
        participation.updateStatus(ParticipationStatus.SUCCESS);
        log.info("참여 확정 성공: participationId={}", participationId);

        if (groupbuy.getStatus() == GroupbuyStatus.COMPLETED) {
            publishGroupbuyConfirmedEvent(groupbuy);
        }

    }

    private void publishGroupbuyConfirmedEvent(Groupbuy groupbuy) {
        try {
            GroupbuyConfirmedEvent event = GroupbuyConfirmedEvent.of(groupbuy.getGroupbuyId(), groupbuy.getProductId());
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(GroupbuyConfirmedEvent.TOPIC, groupbuy.getGroupbuyId(), payload);
            log.info("공동구매 확정 이벤트 발행: groupbuyId={}", groupbuy.getGroupbuyId());
        } catch (Exception e){
            log.error("공동구매 확정 이벤트 발행 실패", e);
        }

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
