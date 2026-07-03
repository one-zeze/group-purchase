package groupbuy_service.payment.event;

import groupbuy_service.order.event.OrderCreatedEvent;
import groupbuy_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

    private final JsonMapper jsonMapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = OrderCreatedEvent.TOPIC, groupId = "groupbuy-service-payment-group")
    public void onOrderCreatedEvent(String message) {
        try {
            OrderCreatedEvent event = jsonMapper.readValue(message, OrderCreatedEvent.class);
            log.info("주문생성 이벤트 수신: groupbuyId={}, orderCnt={}", event.groupbuyId(), event.orders().size());
            // 결제정보 생성 서비스 호출
            paymentService.createPayments(event);

        } catch (Exception e){
            log.error("주문생성 이벤트 수신 실패: {}", message, e);
        }

    }


}
