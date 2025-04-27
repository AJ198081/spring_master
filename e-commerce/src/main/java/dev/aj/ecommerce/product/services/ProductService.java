package dev.aj.ecommerce.product.services;

import dev.aj.ecommerce.product.domain.dtos.ProductRequest;
import dev.aj.ecommerce.product.domain.dtos.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getFeaturedProducts();

    List<ProductResponse> searchProducts(String query);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);
}
