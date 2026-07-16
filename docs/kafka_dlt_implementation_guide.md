# 🛠️ Kafka 재시도(Retry) 및 DLT(Dead Letter Topic) 구현 가이드

카프카 구조에 대한 이해도를 높이면서도, 장애 복구력을 극대화할 수 있는 가장 직관적인 방식인 **`@RetryableTopic` 어노테이션 기반 재시도 및 DLT 파이프라인**을 구축하는 방법입니다.

이 가이드를 통해 `groupbuy-service` (Java)와 `inventory-service` (Kotlin)의 주요 리스너들에 재시도 메커니즘을 적용할 수 있습니다.

---

## 📌 1. Java 리스너 구현 가이드 (`groupbuy-service`)

`groupbuy-service` 내의 리스너(예: `OrderCreatedEventListener.java`)에 다음과 같이 설정을 추가합니다.

### 📝 적용 예시
```java
package groupbuy_service.payment.event;

import groupbuy_service.order.event.OrderCreatedEvent;
import groupbuy_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
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
            attempts = "3", // 최초 1회 + 재시도 2회 = 총 3회
            backoff = @Backoff(delay = 2000, multiplier = 2.0), // 2초 대기 후 재시도, 실패 시 4초 대기 (지수 백오프)
            exclude = { JacksonException.class }, // JSON 파싱 에러 등 재시도해도 안 되는 에러는 즉시 DLT로 이관
            dltStrategy = DltStrategy.FAIL_ON_ERROR, // DltHandler 처리 중 에러 발생 시 예외 throw
            autoCreateTopics = "true" // Retry/DLT 토픽 자동 생성 (로컬 개발 환경용)
    )
    @KafkaListener(topics = OrderCreatedEvent.TOPIC, groupId = "groupbuy-service-payment-group")
    public void onOrderCreatedEvent(String message) throws Exception {
        try {
            OrderCreatedEvent event = jsonMapper.readValue(message, OrderCreatedEvent.class);
            log.info("주문생성 이벤트 수신: groupbuyId={}, orderCnt={}", event.groupbuyId(), event.orders().size());
            paymentService.createPayments(event);
        } catch (Exception e) {
            log.error("주문생성 이벤트 수신 실패 (재시도 유발): {}", message, e);
            throw e; // 🚨 중요: 예외를 다시 던져야 카프카가 재시도를 수행함!
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("🚨 [DLT 감지] 최종 처리 실패 메시지 이관 완료! 토픽: {}, 내용: {}", topic, message);
        // 실무에서는 여기에 외부 슬랙 Webhook을 연동하거나, 별도 관리 DB에 적재하는 로직이 들어갑니다.
    }
}
```

---

## 📌 2. Kotlin 리스너 구현 가이드 (`inventory-service`)

`inventory-service`는 Kotlin 환경이므로 어노테이션 문법이 약간 다릅니다. 예컨대 `OrderCancelledEventListener.kt`에 적용 시 아래와 같이 작성합니다.

### 📝 적용 예시
```kotlin
package inventory_service.event

import inventory_service.service.InventoryService
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component
import tools.jackson.core.JacksonException
import tools.jackson.databind.json.JsonMapper

@Component
class OrderCancelledEventListener(
    private val jsonMapper: JsonMapper,
    private val inventoryService: InventoryService
) {
    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    @RetryableTopic(
        attempts = "3",
        backoff = Backoff(delay = 2000, multiplier = 2.0),
        exclude = [JacksonException::class],
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = ["order.cancelled"], groupId = "inventory-service-group")
    fun onOrderCancelled(message: String) {
        try {
            val event = jsonMapper.readValue(message, OrderCancelledEvent::class.java)
            log.info("주문 취소 이벤트 수신: orderId=${event.orderId}")
            inventoryService.increaseStock(event.productId, event.quantity)
        } catch (e: Exception) {
            log.error("주문 취소 이벤트 처리 실패 (재시도 유발): $message", e)
            throw e // 🚨 중요: 예외를 다시 던져야 재시도 동작!
        }
    }

    @DltHandler
    fun handleDlt(message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        log.error("🚨 [DLT 감지] 최종 처리 실패! 토픽: $topic, 내용: $message")
    }
}
```

---

## 💡 구현 시 주의할 점 & 구조 학습 가이드

1. **자동 생성되는 토픽 네이밍 규격**:
   - 스프링 카프카는 `@RetryableTopic` 사용 시, 원래 토픽 이름에 접미사를 붙여 재시도 및 DLT 토픽을 자동 생성합니다.
   - 예: `payment.completed` 토픽에 대한 재시도 토픽은 `payment.completed-retry-0`, `payment.completed-retry-1`이 되며, DLT 토픽은 `payment.completed-dlt`가 됩니다.
2. **비즈니스 에러 필터링**:
   - `exclude` 리스트를 꼼꼼하게 정의해야 합니다. 데이터 포맷 오류(`JacksonException`)나 잘못된 인자(`IllegalArgumentException`)처럼 다시 시도해도 백프로 실패할 에러들은 재시도 없이 즉시 DLT로 보내 리소스를 낭비하지 않는 것이 실무 설계의 핵심입니다.
3. **트랜잭션 롤백 주의**:
   - 재시도 대상 리스너 내부의 비즈니스 로직에 `@Transactional`이 걸려 있다면, 예외가 발생할 때 DB 데이터는 안전하게 롤백되고 카프카 메시지만 다음 재시도 주기에 다시 들어오는지 확인해야 합니다.
