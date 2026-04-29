package groupbuy_service.product.event;

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
) {
    public static final String TOPIC = "product.created";

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
