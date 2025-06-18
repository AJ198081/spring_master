package dev.aj.full_stack_v5.order.controllers;

import dev.aj.full_stack_v5.InitSecurityUser;
import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.auth.domain.dtos.LoginRequestDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import dev.aj.full_stack_v5.order.domain.entities.Order;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class, InitSecurityUser.class})
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"spring.jpa.hibernate.ddl-auto=create-drop"}
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class OrderControllerTest {

    public static final int INITIALLY_USERS_CREATED = 5;
    public static final int INITIALLY_PRODUCTS_CREATED = 5;
    public static final int INITIAL_INVENTORY_OF_EACH_PRODUCT = 100;
    public static final int MAX_QUANTITY_ALLOWED_IN_ONE_ORDER = 10;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    List<UserResponseDto> usersCreatedInThisSession;
    List<Customer> customersCreatedInThisSession;
    List<Product> productsCreatedInThisSession;
    List<Order> ordersCreatedInThisSession = new ArrayList<>();

    @Autowired
    private InitSecurityUser initSecurityUser;

    private HttpHeaders bearerTokenHeader;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));
        bearerTokenHeader = initSecurityUser.getBearerTokenHeader(restClient);

        usersCreatedInThisSession = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(INITIALLY_USERS_CREATED)
                .map(userRegistration -> {
                    ResponseEntity<UserResponseDto> userRegistrationResponse = restClient.post()
                            .uri("/api/v1/users/")
                            .headers(header -> header.addAll(bearerTokenHeader))
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
                                    .headers(header -> header.addAll(bearerTokenHeader))
                                    .body(customerDto)
                                    .retrieve()
                                    .toEntity(Customer.class)
                                    .getBody();
                        })
                )
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        productsCreatedInThisSession = testDataFactory.generateStreamOfProductRequests()
                .limit(INITIALLY_PRODUCTS_CREATED)
                .map(productRequestDto -> {

                    productRequestDto.setInventory(INITIAL_INVENTORY_OF_EACH_PRODUCT);

                    return restClient.post()
                            .uri("/api/v1/products/")
                            .headers(header -> header.addAll(bearerTokenHeader))
                            .body(productRequestDto)
                            .retrieve()
                            .toEntity(Product.class)
                            .getBody();
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);


        customersCreatedInThisSession.forEach(customer -> {

            int randomProductIndex = new Random().nextInt(productsCreatedInThisSession.size());

            restClient.post()
                    .uri("/api/v1/cartItems/?customerId={customerId}&productId={productId}&quantity={quantity}",
                            customer.getId(),
                            productsCreatedInThisSession.get(randomProductIndex).getId(),
                            new Random().nextInt(1, MAX_QUANTITY_ALLOWED_IN_ONE_ORDER))
                    .headers(header -> header.addAll(bearerTokenHeader))
                    .retrieve()
                    .toBodilessEntity();
        });
    }

    @AfterAll
    void tearDown() {
        restClient = null;
    }

    @Test
//    @RepeatedTest(value = 10, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    void createOrder() {
        Customer customer = customersCreatedInThisSession.stream().findAny().orElseThrow();

        ResponseEntity<Order> orderCreatedResponse = restClient.post()
                .uri("/api/v1/orders/?customerId={customerId}", customer.getId())
                .headers(header -> header.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(Order.class);

        Assertions.assertEquals(HttpStatus.OK, orderCreatedResponse.getStatusCode());
        Assertions.assertNotNull(orderCreatedResponse.getBody());

        Order newOrder = orderCreatedResponse.getBody();

        ResponseEntity<List<Order>> ordersResponse = restClient.get()
                .uri("/api/v1/orders/{customerId}", customer.getId())
                .headers(header -> header.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, ordersResponse.getStatusCode());

        Assertions.assertNotNull(ordersResponse.getBody());
        Assertions.assertFalse(ordersResponse.getBody().isEmpty());

        List<Order> ordersForThisCustomer = ordersResponse.getBody();

        Assertions.assertTrue(ordersForThisCustomer.stream().map(Order::getId).collect(Collectors.toSet()).contains(newOrder.getId()));

        ordersCreatedInThisSession.add(newOrder);
    }

    @Test
    void getOrdersByCustomerId() {
        Customer customer = customersCreatedInThisSession.stream().findAny().orElseThrow();

        ResponseEntity<Void> createResponse = restClient.post()
                .uri("/api/v1/orders/?customerId={customerId}", customer.getId())
                .headers(header -> header.addAll(bearerTokenHeader))
                .retrieve()
                .toBodilessEntity();

        Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        ResponseEntity<Void> ordersResponse = restClient.get()
                .uri("/api/v1/orders/{customerId}", customer.getId())
                .headers(header -> header.addAll(bearerTokenHeader))
                .retrieve()
                .toBodilessEntity();

        Assertions.assertEquals(HttpStatus.OK, ordersResponse.getStatusCode());
    }
}
