package dev.aj.redisson;

import com.github.javafaker.Faker;
import dev.aj.redisson.domain.entities.Product;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    private final Faker faker;

    @Value("${password.special-characters: '!@#$%^'}")
    private char[] specialCharacters;

    @Bean
    public Stream<Product> getProductStream() {

        return Stream.generate(() -> Product.builder()
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence(10))
                .price(Double.valueOf(faker.commerce().price(99.90, 999.99)))
                .build());
    }

    private @Size(min = 8, message = "Password must be at least 8 characters") String getValidPassword() {

        String password = faker.internet().password(8, 50, true, true, true);

        if (!StringUtils.containsAny(password, specialCharacters)) {
            password = password.substring(0, password.length() - 1)
                    .concat(String.valueOf(specialCharacters[faker.random().nextInt(specialCharacters.length)]));
        }

        return password;
    }


}
