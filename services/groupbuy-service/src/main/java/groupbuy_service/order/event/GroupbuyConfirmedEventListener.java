package groupbuy_service.order.event;

import groupbuy_service.groupbuy.event.GroupbuyConfirmedEvent;
import groupbuy_service.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupbuyConfirmedEventListener {

    private final OrderService orderService;
    private final JsonMapper jsonMapper;

    @KafkaListener(topics = GroupbuyConfirmedEvent.TOPIC, groupId = "groupbuy-service-group")
    public void onGroupbuyCOnfirmedEvent(String message) throws Exception{
        try{
            GroupbuyConfirmedEvent event = jsonMapper.readValue(message, GroupbuyConfirmedEvent.class);
            orderService.createOrdersForGroupbuy(event.groupbuyId(), event.productId());
        }
        catch (Exception e){
            log.error("공동구매 확정 이벤트 처리 실패: {}", message, e);
            throw e;
        }

    }

}
