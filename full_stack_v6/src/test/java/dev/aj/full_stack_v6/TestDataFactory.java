package dev.aj.full_stack_v6;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.common.domain.entities.Seller;
import dev.aj.full_stack_v6.common.domain.enums.AddressType;
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
                .stock(faker.random().nextInt(2, 100))
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

    public @NonNull ResponseEntity<Void> putAnExistingProduct(Long productId, Product updatedProduct, RestClient postAuthenticatedClient) {
        return postAuthenticatedClient.put()
                .uri("/{id}", productId)
                .body(updatedProduct)
                .retrieve()
                .toBodilessEntity();
    }

    public Stream<Customer> generateStreamOfCustomerRequests() {

        return Stream.generate(() -> Customer.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .phone(faker.phoneNumber().phoneNumber())
                .build());
    }

    public Stream<Address> generateStreamOfAddressRequests() {

        return Stream.generate(() -> Address.builder()
                .addressType(AddressType.values()[faker.random().nextInt(AddressType.values().length)])
                .street(faker.address().streetAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .pinCode(faker.address().zipCode())
                .country(faker.address().country())
                .build());
    }

    public Stream<Seller> generateStreamOfSellerRequests() {

        List<Address> primaryAddress = generateStreamOfAddressRequests()
                .limit(1)
                .peek(address -> address.setAddressType(AddressType.POSTAL))
                .findFirst()
                .stream()
                .toList();

        return Stream.generate(() -> Seller.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .phone(faker.phoneNumber().phoneNumber())
                .addresses(primaryAddress)
                .build());
    }

    public @NonNull ResponseEntity<Seller> saveSellerProfile(RestClient authenticatedSellerClient) {

        return authenticatedSellerClient.post()
                .uri("/")
                .body(generateStreamOfSellerRequests().limit(1).findFirst().orElseThrow())
                .retrieve()
                .toEntity(Seller.class);
    }

    public void saveCustomerProfile(RestClient authenticatedCustomerClient) {

        authenticatedCustomerClient.post()
                .uri("/")
                .body(generateStreamOfCustomerRequests().limit(1).findFirst().orElseThrow())
                .retrieve()
                .toEntity(Customer.class);
    }
}
