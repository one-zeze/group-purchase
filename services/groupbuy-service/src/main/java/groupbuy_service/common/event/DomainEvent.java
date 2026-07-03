package groupbuy_service.common.event;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DomainEvent {
    @JsonIgnore
    String getTopic();
    @JsonIgnore
    String getKey();
}
