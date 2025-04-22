package dev.aj.redisson.services;

import dev.aj.redisson.domain.entities.Product;
import dev.aj.redisson.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Mono<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Mono<Product> saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Mono<Void> deleteProductById(Long id) {
        return productRepository.deleteById(id);
    }

    public Mono<Long> countAllProducts() {
        return productRepository.count();
    }
    
    public Mono<Product> updateProduct(Long id, Product product) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    return productRepository.save(existingProduct);
                });
    }

    public Mono<Product> partialUpdateProduct(Long id, Product product) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    if (product.getName() != null) {
                        existingProduct.setName(product.getName());
                    }
                    if (product.getDescription() != null) {
                        existingProduct.setDescription(product.getDescription());
                    }
                    if (product.getPrice() != null) {
                        existingProduct.setPrice(product.getPrice());
                    }
                    return productRepository.save(existingProduct);
                });
    }
}
