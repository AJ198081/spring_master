package dev.aj.redisson;

import dev.aj.redisson.repositories.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.time.Duration;

@TestComponent
@RequiredArgsConstructor
public class InitDatabase {

    private final TestData testData;
    private final ProductRepository productRepository;

    @PostConstruct
    public void init() {
        Long totalProducts = productRepository.count().block(Duration.ofSeconds(1));
        
        productRepository.count()
                .filter(productCount -> productCount <= 50)
                .flatMapMany(count -> productRepository.saveAll(testData.getProductStream().limit(50).toList()))
                .then()
                .subscribe();
    }
}
