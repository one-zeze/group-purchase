package groupbuy_service.order.event;

import groupbuy_service.common.event.DomainEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent (
        String eventId,
        int version,
        Instant occurredAt,
        String groupbuyId,
        List<OrderInfo> orders
) implements DomainEvent {

    public static final String TOPIC = "order.created";

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getKey() {
        return groupbuyId;
    }

    public static OrderCreatedEvent of (String groupbuyId, List<OrderInfo> orders){
        return new OrderCreatedEvent(
                UUID.randomUUID().toString()
                ,1
                ,Instant.now()
                ,groupbuyId
                ,orders);
    }

}
