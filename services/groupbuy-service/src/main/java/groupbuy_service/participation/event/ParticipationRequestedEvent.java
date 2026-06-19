package groupbuy_service.participation.event;

import groupbuy_service.common.event.DomainEvent;
import groupbuy_service.participation.domain.Participation;

import java.time.Instant;
import java.util.UUID;

public record ParticipationRequestedEvent(
        String eventId,
        int version,
        Instant occurredAt,
        String participationId,
        String groupbuyId,
        String productId,
        String userId,
        int quantity
) implements DomainEvent {
    public static final String TOPIC = "groupbuy.participation.requested";

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getKey() {
        return productId;
    }

    public static ParticipationRequestedEvent from(
            Participation participation
    ) {
        return new ParticipationRequestedEvent(
                UUID.randomUUID().toString(),
                1,
                Instant.now(),
                participation.getParticipationId(),
                participation.getGroupbuyId(),
                participation.getProductId(),
                participation.getUserId(),
                participation.getQuantity()
        );
    }
}
