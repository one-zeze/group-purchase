package groupbuy_service.participation.event;

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
) {
    public static final String TOPIC = "groupbuy.participation.requested";

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
