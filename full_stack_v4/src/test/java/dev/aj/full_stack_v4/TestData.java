package dev.aj.full_stack_v4;

import com.github.javafaker.Faker;
import dev.aj.full_stack_v4.domain.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TestData {

    private final Faker faker;

    public Stream<Product> generateStreamOfProducts() {
        return Stream.generate(() -> Product.builder()
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence())
                .price(new BigDecimal(faker.commerce().price(1, 1000)))
                .imageUrl(faker.internet().image())
                .build());
    }

}
