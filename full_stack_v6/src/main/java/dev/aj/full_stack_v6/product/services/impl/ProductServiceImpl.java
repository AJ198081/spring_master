package dev.aj.full_stack_v6.product.services.impl;

import dev.aj.full_stack_v6.category.CategoryService;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.product.ProductService;
import dev.aj.full_stack_v6.product.repositories.ProductRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        assertProductNameUniqueness(product);
        product.setCategory(saveOrGetExistingCategory(product.getCategory()));
        productRepository.save(product);
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity ID %d not found".formatted(id)));
    }

    @Override
    public Page<Product> findProductPage(String name, int page, int size, String sortDirection) {
        return productRepository.findPageByNameContainingIgnoreCase(
                name,
                PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), "name"))
        );
    }

    @Override
    public void putProduct(Long id, Product product) {
        assertProductNameUniqueness(product);

        productRepository.findById(id)
                .ifPresentOrElse(existing -> {
                            existing.setCategory(saveOrGetExistingCategory(product.getCategory()));
                            updateProductIdempotent(product, existing);
                        },
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no put operation occurred".formatted(product.getId()));
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
            productRepository.save(existing);
        } else {
            log.info("No changes were made, as the existing entity has the same characteristics as the modified product");
        }
    }

    private void assertProductNameUniqueness(Product product) {
        // Only enforce uniqueness on product name; be null-safe.
        if (product.getName() != null && productRepository.existsByName(product.getName())) {
            throw new EntityExistsException("Product with name '%s' already exists".formatted(product.getName()));
        }
    }

    private Category saveOrGetExistingCategory(Category category) {
        try {
            return categoryService.saveCategory(category);
        } catch (EntityExistsException e) {
            log.info("Category with name {} already exists, fetching existing category", category.getName());
            return categoryService.getCategoryByName(category.getName());
        }
    }
}
