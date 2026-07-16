package groupbuy_service.payment.event;

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
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

    private final JsonMapper jsonMapper;
    private final PaymentService paymentService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
            exclude = {JacksonException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = OrderCreatedEvent.TOPIC, groupId = "groupbuy-service-payment-group")
    public void onOrderCreatedEvent(String message) throws Exception{
        try {
            OrderCreatedEvent event = jsonMapper.readValue(message, OrderCreatedEvent.class);
            log.info("주문생성 이벤트 수신: groupbuyId={}, orderCnt={}", event.groupbuyId(), event.orders().size());
            // 결제정보 생성 서비스 호출
            paymentService.createPayments(event);

        } catch (Exception e){
            log.error("주문생성 이벤트 수신 실패: {}", message, e);
            throw e;
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC)String topic) {
        log.error("[DLT] 주문 생성 실패. topic: {}, content: {}", topic,message);
    }


}
