package dev.aj.full_stack_v6.order.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-performance.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
class OrderControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserAuthFactory userAuthFactory;

    @Autowired
    private Environment environment;

    private RestClient authenticatedPaymentClient;

    private RestClient authenticatedCustomerClient;

    private RestClient authenticatedSellerClient;

    private RestClient authenticatedOrderClient;

    private RestClient authenticatedCartClient;

    private RestClient authenticatedProductClient;

    @BeforeAll
    void init() {
        userAuthFactory.setClients(port);
        userAuthFactory.loginAndReturnNonAdminJwt();
        this.instantiateAuthenticatedClientsForThisUser();
        testDataFactory.saveSellerProfile(authenticatedSellerClient);
        testDataFactory.saveCustomerWithShippingAndResidentialAddress(authenticatedCustomerClient);
    }

    @AfterAll
    void destroy() {
        userAuthFactory.resetClients();
        this.resetClientsForThisUser();
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(OrderAnnotation.class)
    class PostOrderTests {

        private Product savedProduct;
        private String paymentUUID;

        @RepeatedTest(value = 20, name = "{displayName} {currentRepetition}/{totalRepetitions}")
        @org.junit.jupiter.api.Order(1)
        @DisplayName("Test create order")
        void whenValidOrder_WithValidPayment_thenCreatesOrder() {

            ResponseEntity<Void> paymentSubmissionResponse = testDataFactory.submitPaymentRequest(
                    testDataFactory.generateARandomPaymentRequest(),
                    authenticatedPaymentClient
            );

            paymentUUID = testDataFactory.getLocationHeader(paymentSubmissionResponse)
                    .replace("/", "");

            Product newProduct = testDataFactory.getStreamOfProducts()
                    .findFirst()
                    .orElseThrow();

            savedProduct = testDataFactory.saveANewProduct(newProduct, authenticatedProductClient)
                    .getBody();

            Assertions.assertThat(savedProduct).isNotNull();

            int quantityToOrder = savedProduct.getStock() - 1;

            testDataFactory.addProductToUserCart(
                    savedProduct.getId(),
                    quantityToOrder,
                    authenticatedCartClient
            );

            ResponseEntity<Void> orderIdentifier = authenticatedOrderClient.post()
                    .uri("/", uriBuilder -> uriBuilder
                            .queryParam("paymentIdentifier", paymentUUID)
                            .build()
                    )
                    .retrieve()
                    .toBodilessEntity();

            Assertions.assertThat(orderIdentifier)
                    .isNotNull()
                    .satisfies(response -> {
                        response.getStatusCode().is2xxSuccessful();
                        Assertions.assertThat(response.getHeaders().getLocation())
                                .isNotNull()
                                .satisfies(uri -> Assertions.assertThat(uri.toString())
                                        .startsWith("/")
                                        .hasSize(37));
                    });

            String newOrderUri = Objects.requireNonNull(orderIdentifier.getHeaders().getLocation())
                    .toString();

            ResponseEntity<Order> orderResponse = authenticatedOrderClient.get()
                    .uri(newOrderUri)
                    .retrieve()
                    .toEntity(Order.class);

            Assertions.assertThat(orderResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(order -> {

                                            Assertions.assertThat(order)
                                                    .isNotNull()
                                                    .extracting(Order::getPayment)
                                                    .extracting(Payment::getPaymentIdentifier)
                                                    .isEqualTo(UUID.fromString(paymentUUID));

                                            Assertions.assertThat(order)
                                                    .extracting(Order::getTotalPrice)
                                                    .isEqualTo(newProduct.getPrice()
                                                            .multiply(BigDecimal.valueOf(quantityToOrder))
                                                            .add(order.getShippingPrice()));
                                        }
                                );
                    });
        }

        @Test
        @org.junit.jupiter.api.Order(2)
        void whenValidOrder_WithInvalidPayment_thenThrows() {

            testDataFactory.addProductToUserCart(
                    savedProduct.getId(),
                    1,
                    authenticatedCartClient
            );

            Assertions.assertThatThrownBy(
                            () -> authenticatedOrderClient.post()
                                    .uri("/", uriBuilder -> uriBuilder
                                            .queryParam("paymentIdentifier", paymentUUID)
                                            .build()
                                    )
                                    .retrieve()
                                    .toBodilessEntity()
                    )
                    .isInstanceOf(HttpClientErrorException.Conflict.class);

        }
    }

    protected void instantiateAuthenticatedClientsForThisUser() {
        authenticatedPaymentClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("PAYMENT_API_PATH")));
        authenticatedCustomerClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("CUSTOMER_API_PATH")));
        authenticatedSellerClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("SELLER_API_PATH")));
        authenticatedOrderClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("ORDER_API_PATH")));
        authenticatedCartClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("CART_API_PATH")));
        authenticatedProductClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("PRODUCT_API_PATH")));
    }

    private void resetClientsForThisUser() {
        authenticatedPaymentClient = null;
        authenticatedCustomerClient = null;
        authenticatedSellerClient = null;
        authenticatedOrderClient = null;
        authenticatedCartClient = null;
        authenticatedProductClient = null;
    }
}