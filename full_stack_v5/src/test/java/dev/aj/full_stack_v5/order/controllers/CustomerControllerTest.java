package dev.aj.full_stack_v5.order.controllers;

import dev.aj.full_stack_v5.InitSecurityUser;
import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.order.domain.dtos.CustomerDto;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class, InitSecurityUser.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CustomerControllerTest {

    public static final int INITIALLY_CUSTOMER_CREATED = 5;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    List<UserResponseDto> userCreatedInThisSession;
    List<Customer> customersCreatedInThisSession;

    @Autowired
    private InitSecurityUser initSecurityUser;

    private HttpHeaders bearerTokenHeader;


    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));
        bearerTokenHeader = initSecurityUser.getBearerTokenHeader(restClient);

        userCreatedInThisSession = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(INITIALLY_CUSTOMER_CREATED)
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

        customersCreatedInThisSession = userCreatedInThisSession.stream()
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
    }

    @AfterAll
    void tearDown() {
        restClient = null;
    }


    @Test
    void createCustomer() {

        UserResponseDto registeredUser = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(1)
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
                .findFirst().orElseThrow(() -> new NoSuchElementException("Unable to generate stream of UserRegistrationDtos"));


        ResponseEntity<Customer> customerCreationResponse = testDataFactory.generateStreamOfCustomerRequests()
                .limit(INITIALLY_CUSTOMER_CREATED)
                .map(customerDto -> {
                    customerDto.setUsername(registeredUser.getUsername());
                    return restClient.post()
                            .uri("/api/v1/customers/")
                            .headers(header -> header.addAll(bearerTokenHeader))
                            .body(customerDto)
                            .retrieve()
                            .toEntity(Customer.class);
                })
                .findAny().orElseThrow();

        Assertions.assertNotNull(customerCreationResponse.getBody());

        Customer createdCustomer = customerCreationResponse.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.CREATED, customerCreationResponse.getStatusCode()),
                () -> Assertions.assertNotNull(customerCreationResponse.getBody()),
                () -> assertThat(createdCustomer.getUser().getUsername()).isEqualTo(registeredUser.getUsername())
        );

    }

    @Test
    void getCustomerById() {
        Customer customer = customersCreatedInThisSession.stream().findAny().orElseThrow();

        ResponseEntity<Customer> customerResponseEntity = restClient.get()
                .uri("/api/v1/customers/{id}", customer.getId())
                .headers(header -> header.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(Customer.class);

        Assertions.assertNotNull(customerResponseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, customerResponseEntity.getStatusCode());
        Assertions.assertEquals(customer.getId(), customerResponseEntity.getBody().getId());
    }

    @Test
    void getCustomerByUsername() {

        Customer customer = customersCreatedInThisSession.stream().findAny().orElseThrow();

        ResponseEntity<Customer> customerResponseEntity = restClient.get()
                .uri("/api/v1/customers/username/{username}", customer.getUser().getUsername())
                .headers(header -> header.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(Customer.class);

        Assertions.assertNotNull(customerResponseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, customerResponseEntity.getStatusCode());
        Assertions.assertEquals(customer.getId(), customerResponseEntity.getBody().getId());
    }

    @Test
    void updateCustomer() {
        Customer customer = customersCreatedInThisSession.stream().findAny().orElseThrow();

        CustomerDto updateCustomerRequest = testDataFactory.generateStreamOfCustomerRequests()
                .limit(1)
                .findFirst().orElseThrow();

        updateCustomerRequest.setEmail(customer.getEmail());

        ResponseEntity<Customer> customerResponseEntity = restClient.patch()
                .uri("/api/v1/customers/{id}", customer.getId())
                .headers(header -> header.addAll(bearerTokenHeader))
                .body(updateCustomerRequest)
                .retrieve()
                .toEntity(Customer.class);

        Assertions.assertEquals(HttpStatus.OK, customerResponseEntity.getStatusCode());
        Assertions.assertNotNull(customerResponseEntity.getBody());

        Customer updatedCustomer = customerResponseEntity.getBody();

        Assertions.assertEquals(customer.getId(), updatedCustomer.getId());
        Assertions.assertEquals(updateCustomerRequest.getFirstName(), updatedCustomer.getFirstName());
        Assertions.assertEquals(updateCustomerRequest.getLastName(), updatedCustomer.getLastName());
        Assertions.assertAll(
                () -> assertThat(updatedCustomer.getEmail()).isEqualTo(updateCustomerRequest.getEmail()),
                () -> assertThat(updatedCustomer.getPhone()).isEqualTo(updateCustomerRequest.getPhone())
        );
    }

    @Test
    void deleteCustomerById() {
        Customer customer = customersCreatedInThisSession.stream().findAny().orElseThrow();

        ResponseEntity<Void> customerDResponseEntity = restClient.delete()
                .uri("/api/v1/customers/{id}", customer.getId())
                .headers(header -> header.addAll(bearerTokenHeader))
                .retrieve()
                .toBodilessEntity();

        assertThat(customerDResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        customersCreatedInThisSession.remove(customer);

        RestClient.RequestHeadersSpec<?> getCustomerRequest = restClient.get()
                .uri("/api/v1/customers/{id}", customer.getId())
                .headers(header -> header.addAll(bearerTokenHeader));

        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> getCustomerRequest.retrieve().toEntity(Customer.class));

    }
}