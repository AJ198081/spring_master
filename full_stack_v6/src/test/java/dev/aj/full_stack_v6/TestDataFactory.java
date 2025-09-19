package dev.aj.full_stack_v6;

import dev.aj.full_stack_v6.common.domain.entities.Category;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;

import java.math.BigDecimal;
import java.util.stream.Stream;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class TestDataFactory {

    private final Faker faker;

    public Stream<Category> getStreamOfCategories() {
        return Stream.generate(() -> Category.builder()
                .name(faker.commerce().department())
                .build());
    }

    public Stream<Product> getStreamOfProducts() {

        BigDecimal productPrice = BigDecimal.valueOf(faker.random().nextDouble(50, 500));

        return Stream.generate(() -> Product.builder()
                .name(faker.commerce().productName())
                .category(getStreamOfCategories().findFirst().orElseThrow())
                .price(productPrice)
                .description(faker.lorem().sentence())
                .stock(faker.random().nextInt(10, 100))
                .discountedPrice(productPrice.multiply(BigDecimal.valueOf(faker.random().nextDouble(0.5, 0.9))))
                .build());
    }

}
