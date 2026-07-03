package groupbuy_service.order.event;

import groupbuy_service.order.service.OrderService;
import groupbuy_service.payment.domain.PaymentStatus;
import groupbuy_service.payment.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedEventListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = PaymentCompletedEvent.TOPIC, groupId = "groupbuy-service-order")
    public void onPaymentCompleted(String message) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            log.info("결제 완료 이벤트 수신: orderId={}, status={}", event.orderId(), event.status());

            if (event.status() == PaymentStatus.SUCCESS) {
                orderService.completedOrder(event.orderId());
            }
            else if (event.status() == PaymentStatus.FAILED) {
                orderService.cancelOrder(event.orderId());
            }
        }
        catch (Exception e) {
            log.info("결제 완료 이벤트 처리 실패: {}", message, e);
        }

    }

}
