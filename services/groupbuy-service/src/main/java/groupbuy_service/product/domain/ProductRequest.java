package groupbuy_service.product.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ProductRequest(
        String productId,
        @NotBlank
        String name,
        @Min(0)
        BigDecimal price,
        @Min(1)
        int initialStock
) {
}
