package dev.aj.kafka.product.services;

import dev.aj.kafka.product.domain.dto.ProductCreateDto;
import dev.aj.kafka.product.domain.dto.ProductCreatedDto;

public interface ProductService {
    ProductCreatedDto createProduct(ProductCreateDto productCreateDto);

    void deleteAllProducts();
}
