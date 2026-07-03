package groupbuy_service.payment.service;

import groupbuy_service.order.event.OrderCreatedEvent;
import groupbuy_service.payment.domain.Payment;
import groupbuy_service.payment.domain.PaymentStatus;
import groupbuy_service.payment.event.PaymentCompletedEvent;
import groupbuy_service.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createPayments(OrderCreatedEvent event) {
        log.info("결제정보 생성: groupbuyId={}, ordersCount={}", event.groupbuyId(), event.orders().size());

        List<Payment> payments = event.orders().stream()
                .map(orderInfo -> Payment.builder()
                        .orderId(orderInfo.orderId())
                        .userId(orderInfo.userId())
                        .amount(orderInfo.totalPrice())
                        .build()).toList();

        List<Payment> persist_payments = paymentRepository.saveAll(payments);
        payProgress(persist_payments);
    }


    public void payProgress(List<Payment> payments) {

        payments.forEach(payment -> {
            PaymentStatus status = "user-fail".equals(payment.getUserId())
                    ? PaymentStatus.FAILED
                    : PaymentStatus.SUCCESS;

            payment.setStatus(status);
            log.info("결제 승인 처리 완료: orderId={}, userId={}, status={}", payment.getOrderId(), payment.getUserId(), status);

            eventPublisher.publishEvent(PaymentCompletedEvent.of(
                    payment.getPaymentId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getStatus()
            ));
        });
    }



}
