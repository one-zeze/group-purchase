package groupbuy_service.payment.event;

import groupbuy_service.common.event.DomainEvent;
import groupbuy_service.payment.domain.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent (
        String eventId,
        int version,
        Instant occuredAt,
        String paymentId,
        String orderId,
        String userId,
        PaymentStatus status
) implements DomainEvent {

    public static final String TOPIC = "payment.completed";

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getKey() {
        return orderId;
    }

    public static PaymentCompletedEvent of (String paymentId, String orderId, String userId, PaymentStatus status) {
        return new PaymentCompletedEvent(
                UUID.randomUUID().toString(),
                1,
                Instant.now(),
                paymentId,
                orderId,
                userId,
                status);
    }

}
