package groupbuy_service.global.reconciliation.service;

import groupbuy_service.global.reconciliation.FailedEvent;
import groupbuy_service.global.reconciliation.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class DltReconciliationService {

    private final FailedEventRepository failedEventRepository;
    private final JsonMapper jsonMapper;
    private static final String FALLBACK_PAYLOAD = "Serialization failed";

    @Transactional
    public void logFailedEvent(String topic, Object payload, String errorMessage)
    {
        String jsonPayload = serializePayload(payload);

        FailedEvent failedEvent = FailedEvent.builder()
                .topic(topic)
                .payload(jsonPayload)
                .errorMessage(errorMessage)
                .build();
        failedEventRepository.save(failedEvent);
    }

    private String serializePayload(Object payload) {
        String jsonPayload = FALLBACK_PAYLOAD;
        try {
            jsonPayload = jsonMapper.writeValueAsString(payload);
        }catch (JacksonException e) {
            log.error("DLT payload Jackson Serialization failed",e);
        }

        return jsonPayload;
    }

}
