package dev.aj.full_stack_v5.product.service;

import dev.aj.full_stack_v5.product.domain.dtos.ProductRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductResponseDto;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductResponseDto addProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(ProductRequestDto productRequestDto, Long id);

    void deleteProductById(Long id);

    Optional<ProductResponseDto> getProductById(Long id);

    List<ProductResponseDto> getAllProducts();

    List<ProductResponseDto> getAllProductsWithImagesMeta();

    List<ProductResponseDto> getProductsByCategoryName(String categoryName);

    List<ProductResponseDto> getProductsByBrand(String brand);

    List<ProductResponseDto> getProductsByName(String name);

    List<ProductResponseDto> getProductsByCategoryNameAndBrand(String category, String brand);

    List<ProductResponseDto> getProductsByCategoryNameAndProductName(String categoryName, String productName);

    List<ProductResponseDto> getProductsByBrandAndName(String brand, String name);

}
