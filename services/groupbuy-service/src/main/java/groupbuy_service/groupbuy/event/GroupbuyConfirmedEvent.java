package groupbuy_service.groupbuy.event;

import groupbuy_service.common.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record GroupbuyConfirmedEvent(
        String eventId,
        int version,
        Instant occurredAt,
        String groupbuyId,
        String productId
) implements DomainEvent {
    public static final String TOPIC = "groupbuy.confirmed";

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getKey() {
        return groupbuyId;
    }

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
