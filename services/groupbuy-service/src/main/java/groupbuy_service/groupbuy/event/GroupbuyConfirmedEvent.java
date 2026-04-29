package groupbuy_service.groupbuy.event;

import java.time.Instant;
import java.util.UUID;

public record GroupbuyConfirmedEvent(
        String eventId,
        int version,
        Instant occurredAt,
        String groupbuyId,
        String productId
) {
    public static final String TOPIC = "groupbuy.confirmed";

    public static GroupbuyConfirmedEvent of(String groupbuyId, String productId) {
        return new GroupbuyConfirmedEvent(
                UUID.randomUUID().toString(),
                1,
                Instant.now(),
                groupbuyId,
                productId
        );
    }
}
