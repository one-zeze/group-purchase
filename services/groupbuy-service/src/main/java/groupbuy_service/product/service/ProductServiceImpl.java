package groupbuy_service.product.service;

import groupbuy_service.global.BusinessException;
import groupbuy_service.global.ErrorCode;
import groupbuy_service.product.domain.Product;
import groupbuy_service.product.domain.ProductRequest;
import groupbuy_service.product.domain.ProductResponse;
import groupbuy_service.product.event.ProductCreatedEvent;
import groupbuy_service.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ProductResponse registProduct(ProductRequest request) {
        Product product = productRepository.save(
                Product.builder()
                        .name(request.name())
                        .price(request.price())
                        .initialStock(request.initialStock())
                        .build()
        );

        eventPublisher.publishEvent(ProductCreatedEvent.of(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getInitialStock()
        ));

        return ProductResponse.from(product);
    }

    @Override
    public ProductResponse getProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponse.from(product);
    }

    @Override
    public List<ProductResponse> searchList() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

}
