package groupbuy_service.order.event;

import groupbuy_service.global.reconciliation.service.DltReconciliationService;
import groupbuy_service.order.service.OrderService;
import groupbuy_service.payment.domain.PaymentStatus;
import groupbuy_service.payment.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedEventListener {

    private final OrderService orderService;
    private final DltReconciliationService dltReconciliationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
            exclude = {JacksonException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = PaymentCompletedEvent.TOPIC, groupId = "groupbuy-service-order")
    public void onPaymentCompleted(PaymentCompletedEvent event) throws Exception{
        try {
            log.info("결제 완료 이벤트 수신: orderId={}, status={}", event.orderId(), event.status());

            if (event.status() == PaymentStatus.SUCCESS) {
                orderService.completedOrder(event.orderId());
            }
            else if (event.status() == PaymentStatus.FAILED) {
                orderService.cancelOrder(event.orderId());
            }
        }
        catch (Exception e) {
            log.error("결제 완료 이벤트 처리 실패: {}", event, e);
            throw e;
        }

    }

    @DltHandler
    public void handleDlt(PaymentCompletedEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("결제 완료 처리 실패, topic: {}, content: {}", topic, event);
        dltReconciliationService.logFailedEvent(topic, event, "FailedEvent: PaymentCompletedEvent");
    }

}
