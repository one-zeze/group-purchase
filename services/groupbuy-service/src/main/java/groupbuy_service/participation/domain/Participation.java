package groupbuy_service.participation.domain;

import groupbuy_service.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "participation")
@Entity
@NoArgsConstructor
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
  public Participation(String groupbuyId, String productId, String userId, int quantity){
    this.participationId = UUID.randomUUID().toString();
    this.groupbuyId = groupbuyId;
    this. productId = productId;
    this. userId = userId;
    this.quantity = quantity;
  }

}
