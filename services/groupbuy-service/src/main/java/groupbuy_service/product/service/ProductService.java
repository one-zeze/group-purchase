package groupbuy_service.product.service;

import groupbuy_service.product.domain.ProductRequest;
import groupbuy_service.product.domain.ProductResponse;

import java.util.List;

public interface ProductService {
    public ProductResponse registProduct(ProductRequest request);
    public ProductResponse getProduct(String productId);
    public List<ProductResponse> searchList();
}
