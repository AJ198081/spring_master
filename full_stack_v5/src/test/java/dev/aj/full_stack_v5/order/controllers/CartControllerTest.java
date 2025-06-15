package dev.aj.full_stack_v5.order.controllers;

import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CartControllerTest {

    public static final int INITIALLY_USERS_CREATED = 5;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    List<UserResponseDto> usersCreatedInThisSession;
    List<Customer> customersCreatedInThisSession;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));

        usersCreatedInThisSession = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(INITIALLY_USERS_CREATED)
                .map(userRegistration -> {
                    ResponseEntity<UserResponseDto> userRegistrationResponse = restClient.post()
                            .uri("/api/v1/users/")
                            .body(userRegistration)
                            .retrieve()
                            .toEntity(new ParameterizedTypeReference<>() {
                            });

                    Assertions.assertNotNull(userRegistrationResponse.getBody());
                    return userRegistrationResponse.getBody();
                })
                .toList();

        customersCreatedInThisSession = usersCreatedInThisSession.stream()
                .map(UserResponseDto::getUsername)
                .flatMap(username -> testDataFactory.generateStreamOfCustomerRequests()
                        .limit(1)
                        .map(customerDto -> {
                            customerDto.setUsername(username);
                            return restClient.post()
                                    .uri("/api/v1/customers/")
                                    .body(customerDto)
                                    .retrieve()
                                    .toEntity(Customer.class)
                                    .getBody();
                        })
                )
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Test
    void getCartByCustomer() {

        Customer customer = customersCreatedInThisSession.stream().findAny().orElseThrow();

        ResponseEntity<Cart> cartResponse = restClient.get()
                .uri("/api/v1/carts/customer/{customerId}", customer.getId())
                .retrieve()
                .toEntity(Cart.class);

        Assertions.assertEquals(HttpStatus.OK, cartResponse.getStatusCode());
        Assertions.assertNotNull(cartResponse.getBody());
        Cart cart = cartResponse.getBody();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(cart.getId()),
                () -> Assertions.assertNotNull(cart.getCustomer()),
                () -> Assertions.assertEquals(customer.getId(), cart.getCustomer().getId())
        );
    }

    @Test
    void removeItemFromCart() {

        Customer customer = customersCreatedInThisSession.stream()
                .findAny()
                .orElseThrow();

        ResponseEntity<Cart> cartResponse = restClient.get()
                .uri("/api/v1/carts/customer/{customerId}", customer.getId())
                .retrieve()
                .toEntity(Cart.class);

        Assertions.assertEquals(HttpStatus.OK, cartResponse.getStatusCode());
        Assertions.assertNotNull(cartResponse.getBody());
        Cart cart = cartResponse.getBody();

        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/api/v1/carts/{id}", cart.getId())
                .retrieve()
                .toBodilessEntity();

        Assertions.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        RestClient.RequestHeadersSpec<?> cartRequest = restClient.get()
                .uri("/api/v1/carts/{Id}", cart.getId());

        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> cartRequest.retrieve().toEntity(Cart.class));

    }

}
