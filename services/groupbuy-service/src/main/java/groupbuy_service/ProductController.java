package groupbuy_service;

import groupbuy_service.product.domain.ProductRequest;
import groupbuy_service.product.domain.ProductResponse;
import groupbuy_service.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponse registProduct(@RequestBody ProductRequest request) {
        return productService.registProduct(request);
    }

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable String productId) {
        return productService.getProduct(productId);
    }

    @GetMapping
    public List<ProductResponse> searchList() {
        return productService.searchList();
    }
}
