package dev.aj.full_stack_v5.product;

import dev.aj.full_stack_v5.order.domain.entities.CartItem;
import dev.aj.full_stack_v5.product.domain.dtos.ProductRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Brand;
import dev.aj.full_stack_v5.product.domain.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductResponseDto addProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(ProductRequestDto productRequestDto, Long id);

    void updateProduct(Product product);

    void deleteProductById(Long id);

    Optional<ProductResponseDto> getProductResponseDtoByProductId(Long id);

    Optional<Product> getProductById(Long id);

    List<ProductResponseDto> getAllProducts();

    List<ProductResponseDto> getDistinctProductResponseDtos();

    List<ProductResponseDto> getAllProductsWithImagesMeta();

    List<ProductResponseDto> getProductsByCategoryName(String categoryName);

    List<ProductResponseDto> getProductsByBrand(String brand);

    List<ProductResponseDto> getProductsByName(String name);

    List<ProductResponseDto> getProductsByCategoryNameAndBrand(String category, String brand);

    List<ProductResponseDto> getProductsByCategoryNameAndProductName(String categoryName, String productName);

    List<ProductResponseDto> getProductsByBrandAndName(String brand, String name);

    Product updateInventory(CartItem cartItem);

    List<String> getDistinctBrands();

    List<ProductResponseDto> getProductResponseDtosByProductId(Long id);

    Brand saveANewBrand(String brandName);

    List<String> getDistinctCategories();

    ProductResponseDto patchProduct(Long productId, ProductRequestDto productRequestDto);
}
