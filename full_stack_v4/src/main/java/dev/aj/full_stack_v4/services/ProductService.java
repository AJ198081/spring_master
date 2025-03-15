package dev.aj.full_stack_v4.services;

import dev.aj.full_stack_v4.domain.entities.Product;
import dev.aj.full_stack_v4.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product save(Product newProduct) {
        return productRepository.save(newProduct);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Product update(Long id, Product productToBeUpdated) {

        Product existingProduct;

        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            log.info("Product with id {} not found, saving as new product", id);
            return productRepository.save(productToBeUpdated);
        } else {
            existingProduct = optionalProduct.get();
        }

        if (productToBeUpdated.getName() != null) {
            existingProduct.setName(productToBeUpdated.getName());
        }
        if (productToBeUpdated.getDescription() != null) {
            existingProduct.setDescription(productToBeUpdated.getDescription());
        }
        if (productToBeUpdated.getPrice() != null) {
            existingProduct.setPrice(productToBeUpdated.getPrice());
        }
        if (productToBeUpdated.getImageUrl() != null) {
            existingProduct.setImageUrl(productToBeUpdated.getImageUrl());
        }
        log.info("Updating product with id {}", id);
        return productRepository.save(existingProduct);

    }
}
