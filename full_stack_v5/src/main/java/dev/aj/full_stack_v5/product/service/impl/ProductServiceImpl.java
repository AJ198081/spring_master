package dev.aj.full_stack_v5.product.service.impl;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.product.domain.dtos.ProductDto;
import dev.aj.full_stack_v5.product.domain.entities.Category;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import dev.aj.full_stack_v5.product.domain.mappers.ProductMapper;
import dev.aj.full_stack_v5.product.repositories.CartItemRepository;
import dev.aj.full_stack_v5.product.repositories.CartRepository;
import dev.aj.full_stack_v5.product.repositories.CategoryRepository;
import dev.aj.full_stack_v5.product.repositories.OrderItemRepository;
import dev.aj.full_stack_v5.product.repositories.ProductRepository;
import dev.aj.full_stack_v5.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;

    @Override
    public Product addProduct(ProductDto newProduct) {

        if (productRepository.findProductByNameAndBrand(newProduct.getName(), newProduct.getBrand()).isPresent()) {
            throw new IllegalArgumentException("Product with name: %s and brand: %s already exists.".formatted(newProduct.getName(), newProduct.getBrand()));
        }

        Product product = productMapper.toProduct(newProduct);

        product.setCategory(getCategory(newProduct.getCategoryName()));

        return productRepository.save(product);
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
    public Product updateProduct(ProductDto productDto, Long id) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDto.getName());
                    existingProduct.setBrand(productDto.getBrand());
                    existingProduct.setPrice(productDto.getPrice());
                    existingProduct.setInventory(productDto.getInventory());
                    existingProduct.setDescription(productDto.getDescription());
                    existingProduct.setCategory(getCategory(productDto.getCategoryName()));

                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id: " + id + " to update."));
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
    public Optional<Product> getProductById(Long id) {
        return productRepository
                .findById(id);
    }

    @Override
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<ProductDto> getAllProductsWithImagesMeta() {
        return productRepository.findAll().stream().map(productMapper::toProductDto).toList();
    }

    @Override
    public Iterable<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findProductByCategoryName(categoryName);
    }

    @Override
    public Iterable<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public Iterable<Product> getProductsByName(String name) {
        return productRepository.findProductByName(name);
    }

    @Override
    public Iterable<Product> getProductsByCategoryNameAndBrand(String categoryName, String brand) {
        return productRepository.findByCategoryNameAndBrand(categoryName, brand);
    }

    @Override
    public Iterable<Product> getProductsByCategoryNameAndProductName(String categoryName, String productName) {
        return productRepository.findProductByCategoryNameAndName(categoryName, productName);
    }

    @Override
    public Iterable<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }
}
