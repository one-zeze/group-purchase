package groupbuy_service.order.event;

import groupbuy_service.common.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelledEvent(
        String eventId,
        int version,
        Instant occurredAt,
        String participationId,
        String orderId,
        String productId,
        int quantity

) implements DomainEvent {

    public static final String TOPIC = "order.cancelled";

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getKey() {
        return orderId;
    }

    public static OrderCancelledEvent of (String participationId, String orderId, String productId, int quantity) {
        return new OrderCancelledEvent(
                UUID.randomUUID().toString(),
                1,
                Instant.now(),
                participationId,
                orderId,
                productId,
                quantity
        );
    }

}
