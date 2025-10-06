package dev.aj.full_stack_v6.payment.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.entities.PaymentDetails;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
class PaymentControllerTest {

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

    @BeforeAll
    void init() {
        userAuthFactory.setClients(port);
        userAuthFactory.loginAndReturnNonAdminJwt();
        instantiateAuthenticatedClientsForThisUser();
        testDataFactory.saveCustomerProfile(authenticatedCustomerClient);
    }

    @AfterAll
    void destroy() {
        userAuthFactory.resetClients();
        authenticatedPaymentClient = null;
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PostPaymentTests {

        @Test
        void processCardPayment() {

            ResponseEntity<Void> paymentResponse = testDataFactory.submitPaymentRequest(
                    testDataFactory.generateARandomPaymentRequest(),
                    authenticatedPaymentClient
            );

            Assertions.assertThat(paymentResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(CREATED);
                        Assertions.assertThat(response.getHeaders())
                                .isNotNull()
                                .containsKey("Location")
                                .extracting(headers -> headers.get(HttpHeaders.LOCATION)
                                        .getFirst())
                                .satisfies(uri -> Assertions.assertThat(uri)
                                        .isNotNull()
                                        .isNotBlank()
                                        .startsWith("/")
                                        .hasSize(37)
                                );
                    });
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(OrderAnnotation.class)
    class GetPaymentsTests {

        private String paymentUri;
        private String newPaymentUri;

        @Test
        @Order(1)
        void nonAdmin_getOwnPaymentById_Successful() {

            PaymentDetails paymentRequest = testDataFactory.generateARandomPaymentRequest();
            paymentUri = testDataFactory.getLocationHeader(testDataFactory.submitPaymentRequest(paymentRequest, authenticatedPaymentClient));

            ResponseEntity<Payment> paymentResponse = getPaymentRequestByURI(paymentUri);

            Assertions.assertThat(paymentResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(payment -> Assertions.assertThat(payment.getPaymentDetails())
                                        .isNotNull()
                                        .usingRecursiveComparison()
                                        .ignoringFields("id")
                                        .isEqualTo(paymentRequest));
                    });
        }

        @Test
        @Order(2)
        void nonAdmin_getSomeoneElsePayment_throwsForbidden() {
            userAuthFactory.loginAndReturnNonAdminJwt();
            instantiateAuthenticatedClientsForThisUser();
            testDataFactory.saveCustomerProfile(authenticatedCustomerClient);

            PaymentDetails paymentRequest = testDataFactory.generateARandomPaymentRequest();
            newPaymentUri = testDataFactory.getLocationHeader(testDataFactory.submitPaymentRequest(paymentRequest, authenticatedPaymentClient));

            assertDoesNotThrow(() -> getPaymentRequestByURI(newPaymentUri));

            Assertions.assertThatThrownBy(() -> getPaymentRequestByURI(paymentUri))
                    .isInstanceOf(HttpClientErrorException.Forbidden.class);
        }

        @Test
        @Order(3)
        void admin_getSomeoneElsePayment_successful() {
            userAuthFactory.loginAndReturnAdminJwt();
            instantiateAuthenticatedClientsForThisUser();

            assertDoesNotThrow(() -> getPaymentRequestByURI(paymentUri));
            assertDoesNotThrow(() -> getPaymentRequestByURI(newPaymentUri));
        }
    }

    protected void instantiateAuthenticatedClientsForThisUser() {
        authenticatedPaymentClient = null;
        authenticatedCustomerClient = null;
        authenticatedPaymentClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("PAYMENT_API_PATH")));
        authenticatedCustomerClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("CUSTOMER_API_PATH")));
    }

    private @NotNull ResponseEntity<Payment> getPaymentRequestByURI(String paymentUri) {
        return authenticatedPaymentClient.get()
                .uri(paymentUri)
                .retrieve()
                .toEntity(Payment.class);
    }
}