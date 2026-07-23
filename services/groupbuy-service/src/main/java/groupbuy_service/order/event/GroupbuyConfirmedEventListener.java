package groupbuy_service.order.event;

import groupbuy_service.global.reconciliation.service.DltReconciliationService;
import groupbuy_service.groupbuy.event.GroupbuyConfirmedEvent;
import groupbuy_service.order.service.OrderService;
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
public class GroupbuyConfirmedEventListener {

    private final OrderService orderService;
    private final DltReconciliationService reconciliationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 1000, multiplier = 2.0, maxDelay = 20000),
            exclude = {JacksonException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = GroupbuyConfirmedEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onGroupbuyConfirmedEvent(GroupbuyConfirmedEvent event) throws Exception{
        try{
            orderService.createOrdersForGroupbuy(event.groupbuyId(), event.productId());
        }
        catch (Exception e){
            log.error("공동구매 확정 이벤트 처리 실패: {}", event, e);
            throw e;
        }
    }

    @DltHandler
    public void handleDlt(GroupbuyConfirmedEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("공동구매 확정 이벤트 처리 실패, topic: {}, content: {}", topic, event);
        reconciliationService.logFailedEvent(topic, event, "FailedEvent: GroupbuyConfirmedEvent");
    }

}
