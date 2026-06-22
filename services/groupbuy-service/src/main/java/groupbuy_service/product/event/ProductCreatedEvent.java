package groupbuy_service.product.event;

import groupbuy_service.common.event.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductCreatedEvent(
        String eventId,
        int version,
        Instant occurredAt,
        String productId,
        String name,
        BigDecimal price,
        int initialStock
) implements DomainEvent {
    public static final String TOPIC = "product.created";

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getKey() {
        return productId;
    }

    public static ProductCreatedEvent of(String productId, String name, BigDecimal price, int initialStock) {
        return new ProductCreatedEvent(
                UUID.randomUUID().toString(),
                1,
                Instant.now(),
                productId,
                name,
                price,
                initialStock
        );
    }
}
