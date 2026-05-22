package groupbuy_service.groupbuy.domain;

import groupbuy_service.common.BaseEntity;
import groupbuy_service.global.BusinessException;
import groupbuy_service.global.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "groupbuy")
@Entity
@NoArgsConstructor
@Getter
public class Groupbuy extends BaseEntity {
  @Id
  private String groupbuyId;

  private String productId;

  private int targetQuantity;
  private int currentQuantity;
  private LocalDateTime startAt;
  private LocalDateTime endAt;

  @Enumerated(EnumType.STRING)
  private GroupbuyStatus status;

  @Builder
  public Groupbuy(String productId, int targetQuantity, LocalDateTime startAt, LocalDateTime endAt) {
    validateTargetQuantity(targetQuantity);
    validatePeriod(startAt, endAt);

    this.groupbuyId = UUID.randomUUID().toString();
    this.productId = productId;
    this.targetQuantity = targetQuantity;
    this.startAt = startAt;
    this.endAt = endAt;

    this.status = GroupbuyStatus.CLOSED;
  }

  public void updateStatus(GroupbuyStatus status) {
    this.status = status;
  }

  public void setTargetQuantity(int targetQuantity) {
    validateTargetQuantity(targetQuantity);
    this.targetQuantity = targetQuantity;
  }

  public void addCurrentQuantity(int quantity) {
    validateAddQuantity(quantity);
    this.currentQuantity += quantity;
  }

  private void validateTargetQuantity(int targetQuantity) {
    if (targetQuantity <= 0) {
      // Assuming TARGET_QUANTITY_INVALID is conceptually similar to ORDER_QUANTITY_INVALID based on previous code.
      throw new BusinessException(ErrorCode.ORDER_QUANTITY_INVALID); 
    }
  }

  private void validateAddQuantity(int quantity) {
    if (quantity < 1) {
      throw new BusinessException(ErrorCode.ORDER_QUANTITY_INVALID);
    }
  }

  private void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
      if (startAt == null || endAt == null) {
          throw new BusinessException(ErrorCode.INTERNAL_ERROR); 
      }
      
      if (startAt.isAfter(endAt)) {
          throw new BusinessException(ErrorCode.GROUPBUY_STARTAT_INVALID);
      }
      
      if (endAt.isBefore(LocalDateTime.now())) {
          throw new BusinessException(ErrorCode.GROUPBUY_ENDAT_INVALID);
      }
  }

}
