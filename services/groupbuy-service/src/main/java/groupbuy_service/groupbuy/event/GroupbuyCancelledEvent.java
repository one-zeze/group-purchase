package groupbuy_service.groupbuy.event;

import java.time.Instant;
import java.util.UUID;

public record GroupbuyCancelledEvent(
        String eventId,
        int version,
        Instant occurredAt,
        String groupbuyId,
        String productId,
        String reason
) {
    public static final String TOPIC = "groupbuy.cancelled";

    public static GroupbuyCancelledEvent of(String groupbuyId, String productId, String reason) {
        return new GroupbuyCancelledEvent(
                UUID.randomUUID().toString(),
                1,
                Instant.now(),
                groupbuyId,
                productId,
                reason
        );
    }
}
