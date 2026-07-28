package groupbuy_service.global.reconciliation.service;

import groupbuy_service.global.reconciliation.FailedEvent;
import groupbuy_service.global.reconciliation.FailedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DltReconciliationServiceTest {

    @Mock
    private FailedEventRepository failedEventRepository;

    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private DltReconciliationService dltReconciliationService;

    @Test
    void savesSerializedPayloadWhenSerializationSucceeds() throws Exception {
        TestPayload payload = new TestPayload("event-1");
        when(jsonMapper.writeValueAsString(payload))
                .thenReturn("{\"eventId\":\"event-1\"}");

        dltReconciliationService.logFailedEvent(
                "order.created",
                payload,
                "final failure"
        );

        ArgumentCaptor<FailedEvent> captor =
                ArgumentCaptor.forClass(FailedEvent.class);
        verify(failedEventRepository).save(captor.capture());

        FailedEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getFailedEventId()).isNotBlank();
        assertThat(savedEvent.getTopic()).isEqualTo("order.created");
        assertThat(savedEvent.getPayload())
                .isEqualTo("{\"eventId\":\"event-1\"}");
        assertThat(savedEvent.getErrorMessage()).isEqualTo("final failure");
        assertThat(savedEvent.getCreatedAt()).isNotNull();
    }

    @Test
    void savesFallbackPayloadWhenSerializationFails() throws Exception {
        TestPayload payload = new TestPayload("event-1");
        JacksonException serializationException =
                mock(JacksonException.class);
        when(jsonMapper.writeValueAsString(payload))
                .thenThrow(serializationException);

        dltReconciliationService.logFailedEvent(
                "order.created",
                payload,
                "final failure"
        );

        ArgumentCaptor<FailedEvent> captor =
                ArgumentCaptor.forClass(FailedEvent.class);
        verify(failedEventRepository).save(captor.capture());

        assertThat(captor.getValue().getPayload())
                .isEqualTo("Serialization failed");
    }

    private record TestPayload(String eventId) {
    }
}
