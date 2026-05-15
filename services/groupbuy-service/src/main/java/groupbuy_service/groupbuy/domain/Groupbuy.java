package groupbuy_service.groupbuy.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "groupbuy")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Groupbuy {
  @Id
  String groupbuyId;

  String productId;
}
