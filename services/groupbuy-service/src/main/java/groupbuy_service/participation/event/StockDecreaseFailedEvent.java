package groupbuy_service.participation.event;

import java.time.Instant;

public record StockDecreaseFailedEvent(
    String eventId,
    Instant occurredAt,
    String participationId,
    String productId,
    int quantity,
    String errorCode,
    String errorMessage
) {
    public static final String TOPIC = "inventory.stock.decrease-failed";
}
