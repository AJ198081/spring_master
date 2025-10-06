package dev.aj.full_stack_v6.clients.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
class CustomerControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserAuthFactory userAuthFactory;

    @Autowired
    private Environment environment;

    private RestClient authenticatedCustomerClient;

    @BeforeAll
    void init() {
        userAuthFactory.setClients(port);
        instantiateAuthenticatedClientsForThisUser();
    }

    @AfterAll
    void destroy() {
        userAuthFactory.resetClients();
        authenticatedCustomerClient = null;
    }

    @Test
    void whenCreateCustomerWithAddresses_thenReturnsCustomer() {

        userAuthFactory.deleteCurrentUser();
        instantiateAuthenticatedClientsForThisUser();

        Customer newCustomer = testDataFactory.generateStreamOfCustomerRequests().findFirst().orElseThrow();
        List<Address> newAddresses = testDataFactory.generateStreamOfAddressRequests().limit(1)
                .toList();
        newCustomer.setAddresses(newAddresses);

        ResponseEntity<Customer> customerResponse = authenticatedCustomerClient.post()
                .uri("/")
                .body(newCustomer)
                .retrieve()
                .toEntity(Customer.class);

        assertThat(customerResponse)
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getId()).isNotNull();
                    assertThat(response.getBody().getAddresses()).hasSizeGreaterThanOrEqualTo(newAddresses.size());
                });
    }

    @Test
    void whenGetCustomerById_thenReturnsCustomer() {
        Customer customerProfile = testDataFactory.generateStreamOfCustomerRequests()
                .findFirst()
                .orElseThrow();

        ResponseEntity<Customer> customerResponse = authenticatedCustomerClient.post()
                .uri("/")
                .body(customerProfile)
                .retrieve()
                .toEntity(Customer.class);

        assertThat(customerResponse)
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getId()).isNotNull();
                });

        Customer customer = customerResponse.getBody();
        ResponseEntity<Customer> getById = authenticatedCustomerClient.get()
                .uri("/{id}", Objects.requireNonNull(customer).getId())
                .retrieve()
                .toEntity(Customer.class);

        assertThat(getById)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenAdminUser_GetAllCustomers_thenReturnsList() {

        String _ = userAuthFactory.loginAndReturnAdminJwt();
        instantiateAuthenticatedClientsForThisUser();
        testDataFactory.saveCustomerProfile(authenticatedCustomerClient);

        ResponseEntity<List<Customer>> allCustomersResponse = authenticatedCustomerClient.get()
                .uri("/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertThat(allCustomersResponse)
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode())
                            .isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody())
                            .isNotEmpty()
                            .satisfies(customers -> assertThat(customers)
                                    .hasSizeGreaterThanOrEqualTo(1));
                });
    }

    @Test
    void whenNonAdminUser_GetAllCustomers_thenThrowsForbidden() {

        String _ = userAuthFactory.loginAndReturnNonAdminJwt();
        instantiateAuthenticatedClientsForThisUser();
        testDataFactory.saveCustomerProfile(authenticatedCustomerClient);

        RestClient.ResponseSpec allCustomersResponseSpec = authenticatedCustomerClient.get()
                .uri("/all")
                .retrieve();

        Assertions.assertThatThrownBy(allCustomersResponseSpec::toBodilessEntity)
                .isInstanceOf(HttpClientErrorException.Forbidden.class);
    }

    @Test
    void whenPutCustomer_thenAccepted() {

        Customer savedCustomer = authenticatedCustomerClient.post()
                .uri("/")
                .body(testDataFactory.generateStreamOfCustomerRequests().findFirst().orElseThrow())
                .retrieve()
                .body(Customer.class);

        Customer update = new Customer();
        update.setFirstName("Updated");

        ResponseEntity<Void> updateResponse = authenticatedCustomerClient.put()
                .uri("/{id}", Objects.requireNonNull(savedCustomer).getId())
                .body(update)
                .retrieve()
                .toBodilessEntity();

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        ResponseEntity<Customer> getUpdatedCustomer = authenticatedCustomerClient.get()
                .uri("/{id}", savedCustomer.getId())
                .retrieve()
                .toEntity(Customer.class);

        assertThat(getUpdatedCustomer)
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getFirstName()).isEqualTo("Updated");
                });
    }

    @Test
    void whenPerformCRUDOnCustomerAddresses_thenSuccess() {
        Customer newCustomer = testDataFactory.generateStreamOfCustomerRequests().findFirst().orElseThrow();

        Customer savedCustomer = authenticatedCustomerClient.post()
                .uri("/")
                .body(newCustomer)
                .retrieve()
                .body(Customer.class);

        int addressCount = 2;

        List<Address> addresses = testDataFactory.generateStreamOfAddressRequests()
                .limit(addressCount)
                .toList();

        ResponseEntity<Customer> customerWithAddresses = authenticatedCustomerClient.post()
                .uri("/{id}/addresses", Objects.requireNonNull(savedCustomer).getId())
                .body(addresses)
                .retrieve()
                .toEntity(Customer.class);

        assertThat(customerWithAddresses).isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getAddresses()).hasSizeGreaterThanOrEqualTo(addressCount);
                });

        Address firstAddress = Objects.requireNonNull(customerWithAddresses.getBody())
                .getAddresses()
                .getFirst();

        Address addressPatch = new Address();
        String updatedCityName = "New City";
        addressPatch.setCity(updatedCityName);

        ResponseEntity<Customer> customerWithUpdateAddressEntity = authenticatedCustomerClient.put()
                .uri("/{id}/addresses/{addressId}", savedCustomer.getId(), firstAddress.getId())
                .body(addressPatch)
                .retrieve()
                .toEntity(Customer.class);

        assertThat(customerWithUpdateAddressEntity)
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getAddresses()).hasSizeGreaterThanOrEqualTo(addressCount);
                    assertThat(response.getBody().getAddresses().stream()
                            .filter(address -> address.getId().equals(firstAddress.getId()))
                            .findFirst()
                            .orElseThrow()
                            .getCity()
                    ).isEqualTo(updatedCityName);
                });

        ResponseEntity<Void> addressDeleteResponse = authenticatedCustomerClient.delete()
                .uri("/{id}/addresses/{addressIdd}", savedCustomer.getId(), firstAddress.getId())
                .retrieve()
                .toBodilessEntity();
        assertThat(addressDeleteResponse).isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);
    }

    private void instantiateAuthenticatedClientsForThisUser() {
        authenticatedCustomerClient = null;
        authenticatedCustomerClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("CUSTOMER_API_PATH")));
    }
}