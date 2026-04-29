package groupbuy_service.participation.event;

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

    public static ParticipationRequestedEvent of(
            String participationId, String groupbuyId, String productId, String userId, int quantity
    ) {
        return new ParticipationRequestedEvent(
                UUID.randomUUID().toString(),
                1,
                Instant.now(),
                participationId,
                groupbuyId,
                productId,
                userId,
                quantity
        );
    }
}
