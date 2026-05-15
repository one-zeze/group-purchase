package groupbuy_service.product.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "product")
@Entity
@NoArgsConstructor
@Getter
public class Product {
  @Id
  private String productId;

  private String name;
  private BigDecimal price;
  private int initialStock;

  @Builder
  public Product(String name, BigDecimal price, Integer initialStock) {
    this.productId = UUID.randomUUID().toString();
    this.name = name;
    this.price = price;
    this.initialStock = initialStock;
  }

}
