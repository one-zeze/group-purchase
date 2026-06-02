package groupbuy_service.product.domain;

import java.math.BigDecimal;

public record ProductResponse(
        String productId,
        String name,
        BigDecimal price,
        int initialStock)
{

    public static ProductResponse from (Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getInitialStock()
        );
    }

}
