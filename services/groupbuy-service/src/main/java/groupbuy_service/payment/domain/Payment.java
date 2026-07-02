package groupbuy_service.payment.domain;

import groupbuy_service.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    private String paymentId;

    private String orderId;
    private String userId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Builder
    public Payment(String orderId, String userId, BigDecimal amount) {
        this.paymentId = UUID.randomUUID().toString();
        this.status = PaymentStatus.READY;

        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }

}
