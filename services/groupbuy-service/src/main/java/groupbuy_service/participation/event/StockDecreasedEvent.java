package groupbuy_service.participation.event;

import java.time.Instant;

public record StockDecreasedEvent(
    String eventId,
    Instant occurredAt,
    String participationId,
    String productId,
    int quantity
) {
    public static final String TOPIC = "inventory.stock.decreased";
}
