package groupbuy_service.participation.domain;

import groupbuy_service.common.BaseEntity;
import groupbuy_service.global.BusinessException;
import groupbuy_service.global.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "participation")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Participation extends BaseEntity {
  @Id
  private String participationId;

  private String groupbuyId;
  private String productId;
  private String userId;
  private int quantity;

  @Enumerated(EnumType.STRING)
  private ParticipationStatus status;

  @Builder
  public Participation(String groupbuyId, String productId, String userId, int quantity) {
    validateQuantity(quantity);

    this.participationId = UUID.randomUUID().toString();
    this.groupbuyId = groupbuyId;
    this. productId = productId;
    this. userId = userId;
    this.quantity = quantity;
    this.status = ParticipationStatus.REQUESTED;
  }

  public void updateStatus(ParticipationStatus status) {
    this.status = status;
  }

  private void validateQuantity(int quantity) {
    if (quantity < 1) {
      throw new BusinessException(ErrorCode.ORDER_QUANTITY_INVALID);
    }
  }


}
