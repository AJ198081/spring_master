package dev.aj.full_stack_v6;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class TestDataFactory {

    private final Faker faker;
    private final List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

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

    public MockMultipartFile getRandomImageFile() {
        int size = faker.random().nextInt(64, 1024);
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) (i % 256);
        }
        String fileName = (faker.file()
                .fileName(null, null, "png", null))
                .replace("\\", "_");
        return new MockMultipartFile("files", fileName, MediaType.IMAGE_PNG_VALUE, bytes);
    }

    public Stream<UserCreateRequest> getStreamOfUserRequests() {
        return Stream.generate(() -> new UserCreateRequest(
                faker.internet().username(),
                faker.internet().emailAddress(),
                faker.internet().password(),
                roles.get(faker.random().nextInt(roles.size())))
        );
    }

    public UserCreateRequest userCreateRequest(String username) {
        return new UserCreateRequest(
                username,
                faker.internet().emailAddress(),
                faker.internet().password(),
                roles.get(faker.random().nextInt(roles.size()))
        );
    }

    public @NonNull ResponseEntity<Product> saveANewRandomProduct(Product newProduct, RestClient postAuthenticatedClient) {
        return postAuthenticatedClient.post()
                .uri("/")
                .body(newProduct)
                .retrieve()
                .toEntity(Product.class);
    }

}
