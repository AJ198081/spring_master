package dev.aj.full_stack_v4;

import dev.aj.full_stack_v4.services.ProductService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class InitDatabase {

    private final TestData testData;
    private final ProductService productService;

    @PostConstruct
    public void init() {
        testData.generateStreamOfProducts().limit(20).forEach(productService::save);
    }
}
