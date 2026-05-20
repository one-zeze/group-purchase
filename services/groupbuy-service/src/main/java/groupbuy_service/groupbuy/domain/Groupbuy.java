package groupbuy_service.groupbuy.domain;

import groupbuy_service.common.BaseEntity;
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
  LocalDateTime startAt;
  LocalDateTime endAt;

  @Enumerated(EnumType.STRING)
  private GroupbuyStatus status;

  @Builder
  public Groupbuy(String productId, int targetQuantity, LocalDateTime startAt, LocalDateTime endAt) {
    this.groupbuyId = UUID.randomUUID().toString();
    this.productId = productId;
    this.targetQuantity = targetQuantity;
    this.startAt = startAt;
    this.endAt = endAt;
  }

  public void updateStatus(GroupbuyStatus status) {
    this.status = status;
  }

  public void setTargetQuantity(int targetQuantity) {
    if (targetQuantity <= 0){
      //exception
    }
    this.targetQuantity = targetQuantity;
  }

  public void addCurrentQuantity(int quantity) {
    if (quantity <= 0) {
      //exception
    }
    this.currentQuantity += quantity;

  }


}
