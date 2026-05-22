package groupbuy_service.product.domain;

import groupbuy_service.common.BaseEntity;
import groupbuy_service.global.BusinessException;
import groupbuy_service.global.ErrorCode;
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
public class Product extends BaseEntity {
  @Id
  private String productId;

  private String name;
  private BigDecimal price;
  private int initialStock;

  @Builder
  public Product(String name, BigDecimal price, int initialStock) {
    validatePrice(price);
    validateInitialStock(initialStock);

    this.productId = UUID.randomUUID().toString();
    this.name = name;
    this.price = price;
    this.initialStock = initialStock;
  }

  public void updateStock(int stock) {
    validateInitialStock(stock);
    this.initialStock = stock;
  }

  public void updatePrice(BigDecimal price) {
    validatePrice(price);
    this.price = price;
  }

  public void updateName(String name){
    this.name = name;
  }

  private void validateInitialStock(int initialStock) {
    if (initialStock < 0) {
      throw new BusinessException(ErrorCode.INITIAL_STOCK_INVALID);
    }
  }

  private void validatePrice(BigDecimal price) {
    if (price.compareTo(BigDecimal.ZERO) < 0) {
      throw new BusinessException(ErrorCode.PRODUCT_PRICE_INVALID);
    }
  }

}
