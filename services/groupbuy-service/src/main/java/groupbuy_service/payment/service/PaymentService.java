package groupbuy_service.payment.service;

import groupbuy_service.order.event.OrderCreatedEvent;

public interface PaymentService {

    void createPayments(OrderCreatedEvent event);
}
