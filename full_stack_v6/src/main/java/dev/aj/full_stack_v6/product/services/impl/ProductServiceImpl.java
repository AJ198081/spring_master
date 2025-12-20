package dev.aj.full_stack_v6.product.services.impl;

import dev.aj.full_stack_v6.category.CategoryService;
import dev.aj.full_stack_v6.clients.SellerService;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.common.domain.entities.Seller;
import dev.aj.full_stack_v6.common.domain.entities.User;
import dev.aj.full_stack_v6.common.domain.events.ProductPriceUpdatedEvent;
import dev.aj.full_stack_v6.product.ProductService;
import dev.aj.full_stack_v6.product.repositories.ProductRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service(value = "productService")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final SellerService sellerService;

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        log.info("Received request to save the product: {}", product.getName());
        assertProductNameUniqueness(product);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Seller userSeller = sellerService.getSellerByUsername(user.getUsername())
                .orElseThrow(() -> new IllegalStateException("No Seller Profile found for the authenticated user"));
        product.setSeller(userSeller);

        product.setCategory(saveOrGetExistingCategory(product.getCategory()));
        Product savedProduct = productRepository.save(product);
        log.info("Product: {} saved successfully with Product Id: {}", savedProduct.getName(), savedProduct.getId());
        return savedProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product Id: %d not found".formatted(id)));
    }

    @Override
    public Page<Product> findProductPage(String name, int page, int size, String sortDirection) {

        return productRepository.findPageByNameContainingIgnoreCase(
                name,
                PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), "name"))
        );
    }

    @Override
    public void putProduct(@NonNull Long id, @NonNull Product productToBeUpdated) {
        if (productRepository.findById(id)
                .filter(existingProduct -> !existingProduct.getName().equals(productToBeUpdated.getName()))
                .isPresent()) {
            assertProductNameUniqueness(productToBeUpdated);
        }

        productRepository.findById(id)
                .ifPresentOrElse(existing -> {
                            existing.setCategory(saveOrGetExistingCategory(productToBeUpdated.getCategory()));
                            updateProductIdempotent(productToBeUpdated, existing);
                        },
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no put operation occurred".formatted(productToBeUpdated.getId()));
                        });
    }

    @Override
    public void patchProduct(Long id, Product product) {
        assertProductNameUniqueness(product);
        productRepository.findById(id)
                .ifPresentOrElse(existing -> {
                            if (product.getCategory() != null && !product.getCategory().getName().equals(existing.getCategory().getName())) {
                                existing.setCategory(saveOrGetExistingCategory(product.getCategory()));
                            }
                            updateProductIdempotent(product, existing);
                        },
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no patch occurred".formatted(product.getId()));
                        });
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        () -> log.warn("Entity ID {} was not found, hence no deletion occurred", id));
    }

    private void updateProductIdempotent(Product modifiedProduct, Product existing) {
        if (!existing.getName().equals(modifiedProduct.getName())) {
            existing.setName(modifiedProduct.getName());
        } else {
            log.info("No changes were made, as the existing entity has the same characteristics as the modified product");
        }

        if(!existing.getPrice().equals(modifiedProduct.getPrice())) {
            existing.setPrice(modifiedProduct.getPrice());
            eventPublisher.publishEvent(new ProductPriceUpdatedEvent(existing.getId()));
        } else {
            log.info("No changes were made, no change to the price of the modified Product");
        }
        productRepository.save(existing);
    }

    private void assertProductNameUniqueness(Product product) {
        if (product.getName() != null && productRepository.existsByName(product.getName())) {
            throw new EntityExistsException("Product with name '%s' already exists".formatted(product.getName()));
        }
    }

    private Category saveOrGetExistingCategory(Category category) {
        try {
            return categoryService.saveCategory(category);
        } catch (EntityExistsException e) {
            log.info("Category with name {} already exists, fetching the existing category", category.getName());
            return categoryService.getCategoryByName(category.getName());
        }
    }
}
