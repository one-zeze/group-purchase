package groupbuy_service.participation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "participation")
@Entity
@NoArgsConstructor
@Getter
public class Participation {
  @Id
  String participationId;

  String groupbuyId;
  String productId;
  String userId;
  int quantity;

  @Builder
  public Participation(String groupbuyId, String productId, String userId, int quantity){
    this.participationId = UUID.randomUUID().toString();
    this.groupbuyId = groupbuyId;
    this. productId = productId;
    this. userId = userId;
    this.quantity = quantity;
  }

}
