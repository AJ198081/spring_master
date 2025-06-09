package dev.aj.full_stack_v5.product.service;

import dev.aj.full_stack_v5.product.domain.dtos.ProductDto;
import dev.aj.full_stack_v5.product.domain.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product addProduct(ProductDto productDto);

    Product updateProduct(ProductDto productDto, Long id);

    void deleteProductById(Long id);

    Optional<Product> getProductById(Long id);

    Iterable<Product> getAllProducts();

    List<ProductDto> getAllProductsWithImagesMeta();

    Iterable<Product> getProductsByCategoryName(String categoryName);

    Iterable<Product> getProductsByBrand(String brand);

    Iterable<Product> getProductsByName(String name);

    Iterable<Product> getProductsByCategoryNameAndBrand(String category, String brand);

    Iterable<Product> getProductsByCategoryNameAndProductName(String categoryName, String productName);

    Iterable<Product> getProductsByBrandAndName(String brand, String name);

}
