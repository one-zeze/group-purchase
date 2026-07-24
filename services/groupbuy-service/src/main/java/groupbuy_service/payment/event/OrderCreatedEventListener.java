package groupbuy_service.payment.event;

import groupbuy_service.global.reconciliation.service.DltReconciliationService;
import groupbuy_service.order.event.OrderCreatedEvent;
import groupbuy_service.payment.service.PaymentService;
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
public class OrderCreatedEventListener {

    private final PaymentService paymentService;
    private final DltReconciliationService dltReconciliationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
            exclude = {JacksonException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = OrderCreatedEvent.TOPIC, groupId = "groupbuy-service-payment-group")
    public void onOrderCreatedEvent(OrderCreatedEvent event) throws Exception{
        log.info("주문생성 이벤트 수신: groupbuyId={}, orderCnt={}", event.groupbuyId(), event.orders().size());
        // 결제정보 생성 서비스 호출
        paymentService.createPayments(event);
    }

    @DltHandler
    public void handleDlt(
            OrderCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC)String topic,
            @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage
    ) {
        String errorMessage = exceptionMessage != null
                ? exceptionMessage
                : "FailedEvent: OrderCreatedEvent";

        log.error("[DLT] 주문 생성 실패. topic: {}, event: {}, content: {}", topic, event, errorMessage);
        dltReconciliationService.logFailedEvent(topic, event, errorMessage);
    }


}
