package groupbuy_service.common.event;

public interface DomainEvent {
    String getTopic();
    String getKey();
}
