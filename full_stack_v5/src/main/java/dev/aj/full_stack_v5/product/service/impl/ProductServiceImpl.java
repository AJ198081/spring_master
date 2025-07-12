package dev.aj.full_stack_v5.product.service.impl;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.CartItem;
import dev.aj.full_stack_v5.order.repositories.CartItemRepository;
import dev.aj.full_stack_v5.order.repositories.CartRepository;
import dev.aj.full_stack_v5.order.repositories.OrderItemRepository;
import dev.aj.full_stack_v5.product.ProductService;
import dev.aj.full_stack_v5.product.domain.dtos.ProductRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Category;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import dev.aj.full_stack_v5.product.domain.mappers.ProductMapper;
import dev.aj.full_stack_v5.product.repositories.CategoryRepository;
import dev.aj.full_stack_v5.product.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@NullMarked
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto addProduct(ProductRequestDto newProduct) {

        if (productRepository.findProductByNameAndBrand(newProduct.getName(), newProduct.getBrand()).isPresent()) {
            throw new IllegalArgumentException("Product with name: %s and brand: %s already exists.".formatted(newProduct.getName(), newProduct.getBrand()));
        }

        Product product = productMapper.toProduct(newProduct);

        product.setCategory(getCategory(newProduct.getCategoryName()));

        return productMapper.toProductResponseDto(productRepository.save(product));
    }

    private Category getCategory(String categoryName) {
        return categoryRepository.findCategoryByName(categoryName)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .name(categoryName)
                                .build()
                ));
    }

    @Override
    public ProductResponseDto updateProduct(ProductRequestDto productRequestDto, Long id) {
        return productMapper.toProductResponseDto(productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productRequestDto.getName());
                    existingProduct.setBrand(productRequestDto.getBrand());
                    existingProduct.setPrice(productRequestDto.getPrice());
                    existingProduct.setInventory(productRequestDto.getInventory());
                    existingProduct.setDescription(productRequestDto.getDescription());
                    existingProduct.setCategory(getCategory(productRequestDto.getCategoryName()));

                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id: " + id + " to update.")));
    }

    @Override
    public void updateProduct(Product product) {

        productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {

        productRepository.findById(id)
                .ifPresent(product -> {

                    cartItemRepository.findCartItemByProductId(product.getId())
                            .forEach(cartItem -> {
                                Cart cart = cartItem.getCart();
                                cart.getCartItems().remove(cartItem);
                                cartItemRepository.delete(cartItem);
                                cart.updateTotal();
                                cartRepository.save(cart);
                            });

                    orderItemRepository.findOrderItemByProductId(product.getId())
                            .forEach(orderItem -> {
                                orderItem.setProduct(null);
                                orderItem.getOrder().getOrderItems().remove(orderItem);
                                orderItemRepository.delete(orderItem);
                            });

                    Optional.ofNullable(product.getCategory())
                            .ifPresent(category -> {
                                category.getProducts().remove(product);
                                product.setCategory(null);
                            });

                    productRepository.delete(product);
                });
    }

    @Override
    public Optional<ProductResponseDto> getProductResponseDtoByProductId(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toProductResponseDto);
    }

    public List<Product> getDistinctProductsByName() {
        Map<String, Product> uniqueProductMap = productRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Product::getName,
                        Function.identity(),
                        (existingProduct, conflictingProduct) -> existingProduct)
                );

        return uniqueProductMap.values()
                .stream()
                .toList();
    }

    public List<ProductResponseDto> getDistinctProductResponseDtos() {
        return getDistinctProductsByName().stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }


    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getAllProductsWithImagesMeta() {
        return productRepository.findAll().stream().map(productMapper::toProductResponseDto).toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByCategoryName(String categoryName) {
        return productRepository.findProductByCategoryName(categoryName)
                .stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand)
                .stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByName(@NonNull String name) {
        return productRepository.findProductsByName(name)
                .stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByCategoryNameAndBrand(String categoryName, String brand) {
        return productRepository.findByCategoryNameAndBrand(categoryName, brand)
                .stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByCategoryNameAndProductName(String categoryName, String productName) {
        return productRepository.findProductByCategoryNameAndName(categoryName, productName)
                .stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name)
                .stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public Product updateInventory(CartItem cartItem) {
        return productRepository.findById(cartItem.getProduct().getId())
                .map(product -> {
                            if (product.getInventory() < cartItem.getQuantity()) {
                                throw new IllegalArgumentException("Product with id: %s doesn't have enough inventories.".formatted(product.getId()));
                            }
                            product.setInventory(product.getInventory() - cartItem.getQuantity());
                            return productRepository.save(product);
                        }
                )
                .orElseThrow(() -> new EntityNotFoundException("Product with id: %s doesn't exist.".formatted(cartItem.getProduct().getId())));
    }

    @Override
    public List<String> getDistinctBrands() {
        return productRepository.getDistinctByBrand();
    }

    @Override
    public List<ProductResponseDto> getProductResponseDtosByProductId(Long id) {
        
        return productRepository.findById(id)
                .map(Product::getName)
                .map(productRepository::findProductsByName)
                .map(productMapper::toProductResponseDtos)
                .orElseGet(List::of);
    }
}
