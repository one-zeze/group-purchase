package groupbuy_service.global.reconciliation.service;

import groupbuy_service.global.reconciliation.FailedEvent;
import groupbuy_service.global.reconciliation.FailedEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@Transactional
class DltReconciliationServiceIntegrationTest {

    @Autowired
    private DltReconciliationService dltReconciliationService;

    @Autowired
    private FailedEventRepository failedEventRepository;

    @Test
    void persistsFailedEventInDatabase() {
        String topic = "test.dlt." + System.nanoTime();

        dltReconciliationService.logFailedEvent(
                topic,
                new TestPayload("event-1"),
                "integration test failure"
        );

        FailedEvent savedEvent = failedEventRepository.findAll().stream()
                .filter(event -> topic.equals(event.getTopic()))
                .findFirst()
                .orElseThrow();

        assertThat(savedEvent.getFailedEventId()).isNotBlank();
        assertThat(savedEvent.getPayload()).contains("\"eventId\":\"event-1\"");
        assertThat(savedEvent.getErrorMessage())
                .isEqualTo("integration test failure");
        assertThat(savedEvent.getCreatedAt()).isNotNull();
    }

    private record TestPayload(String eventId) {
    }
}
