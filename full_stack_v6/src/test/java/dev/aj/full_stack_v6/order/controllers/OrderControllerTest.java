package dev.aj.full_stack_v6.order.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.common.domain.enums.OrderStatus;
import dev.aj.full_stack_v6.common.domain.enums.PaymentStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.moments.DayHasPassed;
import org.springframework.modulith.moments.support.TimeMachine;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.AssertablePublishedEvents;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES;

@RecordApplicationEvents
@ApplicationModuleTest(webEnvironment = RANDOM_PORT, mode = ALL_DEPENDENCIES)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {
        "/application-performance.properties",
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
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

    @Autowired
    private TimeMachine timeMachine;

    private RestClient authenticatedPaymentClient;

    private RestClient authenticatedCustomerClient;

    private RestClient authenticatedSellerClient;

    private RestClient authenticatedOrderClient;

    private RestClient authenticatedCartClient;

    private RestClient authenticatedProductClient;

    @BeforeEach
    void init() {
        userAuthFactory.setClients(port);
        userAuthFactory.loginAndReturnNonAdminJwt();
        this.instantiateAuthenticatedClientsForThisUser();
        testDataFactory.saveSellerProfile(authenticatedSellerClient);
        testDataFactory.saveCustomerWithShippingAndResidentialAddress(authenticatedCustomerClient);
    }

    @AfterEach
    void destroy() {
        userAuthFactory.resetClients();
        this.resetClientsForThisUser();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    @TestMethodOrder(OrderAnnotation.class)
    @Execution(ExecutionMode.CONCURRENT)
    class PostOrderTests {

        @SneakyThrows
        @RepeatedTest(value = 10, name = "{displayName} {currentRepetition}/{totalRepetitions}")
        @org.junit.jupiter.api.Order(1)
        @DisplayName("Test create order")
        void whenValidOrder_WithValidPayment_thenCreatesOrder(AssertablePublishedEvents assertablePublishedEvents) {

            TimeUnit.of(ChronoUnit.SECONDS).sleep(5);

            ResponseEntity<Void> paymentSubmissionResponse = testDataFactory.submitPaymentRequest(
                    testDataFactory.generateARandomPaymentRequest(),
                    authenticatedPaymentClient
            );

            String paymentUUID = testDataFactory.getLocationHeader(paymentSubmissionResponse)
                    .replace("/", "");

            Product newProduct = testDataFactory.getStreamOfProducts()
                    .findFirst()
                    .orElseThrow();

            Product savedProduct = testDataFactory.saveANewProduct(newProduct, authenticatedProductClient)
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
                                                    .satisfies(payment -> {
                                                        Assertions.assertThat(payment.getPaymentIdentifier().toString())
                                                                .isEqualTo(paymentUUID);

                                                        assertThat(payment.getPaymentStatus())
                                                                .isEqualTo(PaymentStatus.PENDING);
                                                    });

                                            Assertions.assertThat(order)
                                                    .extracting(Order::getTotalPrice)
                                                    .as("Total order price, including shipping")
                                                    .isEqualTo(newProduct.getPrice()
                                                            .multiply(BigDecimal.valueOf(quantityToOrder))
                                                            .add(order.getShippingPrice()));

                                            Assertions.assertThat(order.getOrderStatus().toString())
                                                    .as("Order status")
                                                    .isEqualTo("NEW");
                                        }
                                );
                    });

            timeMachine.shiftBy(Duration.of(1, ChronoUnit.DAYS));

            assertablePublishedEvents.assertThat()
                    .describedAs("Published Events")
                    .contains(DayHasPassed.class);

            ResponseEntity<Payment> updatedPaymentResponse = authenticatedPaymentClient.get()
                    .uri("/{paymentIdentifier}", Objects.requireNonNull(orderResponse.getBody())
                            .getPayment()
                            .getPaymentIdentifier()
                            .toString())
                    .retrieve()
                    .toEntity(Payment.class);

            Assertions.assertThat(updatedPaymentResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(updatedPayment -> Assertions.assertThat(updatedPayment).isNotNull()
                                        .satisfies(payment -> {
                                            Assertions.assertThat(payment.getPaymentIdentifier().toString())
                                                    .as("Payment identifier")
                                                    .isEqualTo(paymentUUID);

                                            assertThat(payment.getPaymentStatus())
                                                    .as("Payment status")
                                                    .isEqualTo(PaymentStatus.COMPLETED);
                                        })
                                );
                    });

            ResponseEntity<Order> orderResponseOnNextDay = authenticatedOrderClient.get()
                    .uri(newOrderUri)
                    .retrieve()
                    .toEntity(Order.class);

            Assertions.assertThat(orderResponseOnNextDay)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(nextDayResponse -> Assertions.assertThat(nextDayResponse)
                                        .isNotNull()
                                        .as("Order status")
                                        .satisfies(order -> assertThat(order.getOrderStatus())
                                                .as("Order status next day")
                                                .isEqualTo(OrderStatus.COMPLETED))
                                        .extracting(Order::getPayment)
                                        .satisfies(payment -> {
                                            Assertions.assertThat(payment.getPaymentIdentifier().toString())
                                                    .as("Payment identifier next day")
                                                    .isEqualTo(paymentUUID);
                                            assertThat(payment.getPaymentStatus())
                                                    .as("Payment status next day")
                                                    .isEqualTo(PaymentStatus.COMPLETED);
                                        })
                                );
                    });
        }

        @Test
        @org.junit.jupiter.api.Order(2)
        void whenValidOrder_WithInvalidPayment_thenThrows() {

            Product savedProduct = testDataFactory.saveANewProduct(
                            testDataFactory.getStreamOfProducts()
                                    .findFirst()
                                    .orElseThrow(),
                            authenticatedProductClient)
                    .getBody();

            org.junit.jupiter.api.Assertions.assertNotNull(savedProduct);

            testDataFactory.addProductToUserCart(
                    savedProduct.getId(),
                    1,
                    authenticatedCartClient
            );

            ResponseEntity<Void> paymentSubmissionResponse = testDataFactory.submitPaymentRequest(
                    testDataFactory.generateARandomPaymentRequest(),
                    authenticatedPaymentClient
            );

            String paymentUUID = testDataFactory.getLocationHeader(paymentSubmissionResponse)
                    .replace("/", "");

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> authenticatedOrderClient.post()
                    .uri("/", uriBuilder -> uriBuilder
                            .queryParam("paymentIdentifier", paymentUUID)
                            .build()
                    )
                    .retrieve()
                    .toBodilessEntity());

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