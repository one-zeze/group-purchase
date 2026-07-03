package groupbuy_service.order.event;

import java.math.BigDecimal;

public record OrderInfo(
        String orderId,
        String userId,
        BigDecimal totalPrice
) {}
