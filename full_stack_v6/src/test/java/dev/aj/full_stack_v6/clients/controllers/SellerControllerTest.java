package dev.aj.full_stack_v6.clients.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Seller;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
class SellerControllerTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserAuthFactory userAuthFactory;

    @Autowired
    private Environment environment;

    private RestClient sellerClient;

    private Seller sellerProfile;

    private static final String PHONE_NUMBER = "0430 303 330";

    @BeforeAll
    void init() {
        userAuthFactory.setClients(port);
        userAuthFactory.loginAndReturnNonAdminJwt();
        instantiateClients();

        sellerProfile = testDataFactory.saveSellerProfile(sellerClient)
                .getBody();
    }

    @AfterAll
    void destroy() {
        userAuthFactory.resetClients();
        sellerClient = null;
    }

    @Test
    @Order(1)
    void whenCreateSellerWithAddresses_thenReturnsSeller() {

        ResponseEntity<Seller> sellerResponseEntity = testDataFactory.saveSellerProfile(sellerClient);

        assertThat(sellerResponseEntity)
                .isNotNull()
                .satisfies(response -> {
                    assertThat(response.getStatusCode())
                            .isEqualTo(HttpStatus.OK);

                    assertThat(response.getBody())
                            .isNotNull();

                    assertThat(response.getBody().getId())
                            .isNotNull();

                    assertThat(response.getBody().getAddresses())
                            .hasSizeGreaterThanOrEqualTo(1);
                });
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(2)
    class TestSellerRetrieval {

        @Test
        @Order(1)
        void when_Seller_Requests_Own_Profile_thenReturnsSeller() {

            ResponseEntity<Seller> sellerResponse = getSellerProfileById(sellerProfile.getId())
                    .toEntity(Seller.class);

            assertThat(sellerResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull();
                        assertThat(response.getBody().getId())
                                .isNotNull();
                    });
        }

        @Test
        @Order(2)
        void when_Admin_Requests_Other_Seller_Profile_thenReturnsSeller() {

            userAuthFactory.loginAndReturnAdminJwt();
            instantiateClients();
            testDataFactory.saveSellerProfile(sellerClient);

            ResponseEntity<Seller> sellerResponse = getSellerProfileById(sellerProfile.getId())
                    .toEntity(Seller.class);

            assertThat(sellerResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull();
                        assertThat(response.getBody().getId())
                                .isNotNull();
                    });
        }

        @Test
        @Order(3)
        void when_Admin_Requests_All_Seller_Profiles_thenReturnsSeller() {

            ResponseEntity<List<Seller>> allSellers = getAllSellerProfiles()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            assertThat(allSellers)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull();
                        assertThat(response.getBody())
                                .hasSizeGreaterThanOrEqualTo(2);
                    });
        }

        @Test
        @Order(4)
        void whenSeller_Requests_Someone_Else_Profile_thenThrowsForbidden() {

            userAuthFactory.loginAndReturnNonAdminJwt();
            instantiateClients();
            testDataFactory.saveSellerProfile(sellerClient);

            Assertions.assertThatThrownBy(() -> getSellerProfileById(sellerProfile.getId()).toBodilessEntity())
                    .isInstanceOf(HttpClientErrorException.Forbidden.class);
        }

        @Test
        @Order(5)
        void when_Non_Admin_Requests_All_Seller_Profiles_thenThrowsForbidden() {

            Assertions.assertThatThrownBy(() -> getAllSellerProfiles().toBodilessEntity())
                    .isInstanceOf(HttpClientErrorException.Forbidden.class);

        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(3)
    class TestPutPatchSellers {

        private Seller newSeller;
        private Address newAddress;

        @BeforeAll
        void init() {
            userAuthFactory.loginAndReturnNonAdminJwt();
            instantiateClients();
            newSeller = testDataFactory.saveSellerProfile(sellerClient)
                    .getBody();

            newAddress = testDataFactory.generateStreamOfAddressRequests()
                    .limit(1)
                    .findFirst()
                    .orElseThrow();
        }

        @Test
        @Order(1)
        void when_Non_Admin_Puts_Own_Profile_thenUpdates() {

            final String CITY_NAME = "New Put City";

            newAddress.setCity(CITY_NAME);

            Seller putSeller = newSeller.deepClone();

            putSeller.setPhone(null);
            putSeller.setAddresses(List.of(newAddress));

            ResponseEntity<Seller> putResponseEntity = putSellerById(newSeller.getId(), putSeller)
                    .toEntity(Seller.class);

            assertThat(putResponseEntity)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull();
                        assertThat(response.getBody().getId())
                                .isNotNull()
                                .isEqualTo(newSeller.getId());
                    })
                    .extracting(ResponseEntity::getBody)
                    .satisfies(updatedSeller -> {
                        assertThat(updatedSeller.getPhone())
                                .isNull();

                        assertThat(updatedSeller.getAddresses())
                                .isNotEmpty()
                                .hasSize(1)
                                .satisfies(addresses -> assertThat(addresses.getFirst().getCity()).isEqualTo(CITY_NAME));
                    });
        }

        @Test
        @Order(2)
        void when_Non_Admin_Patch_Own_Profile_thenUpdates() {

            final String CITY_NAME = "New Patch City";

            newAddress.setCity(CITY_NAME);

            Seller patchSeller = newSeller.deepClone();

            patchSeller.setPhone(PHONE_NUMBER);
            patchSeller.setAddresses(List.of(newAddress));

            ResponseEntity<Seller> patchResponseEntity = patchSellerProfileById(newSeller.getId(), patchSeller)
                    .toEntity(Seller.class);

            assertThat(patchResponseEntity)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull();
                        assertThat(response.getBody().getId())
                                .isNotNull()
                                .isEqualTo(newSeller.getId());
                    })
                    .extracting(ResponseEntity::getBody)
                    .satisfies(updatedSeller -> {

                        assertThat(updatedSeller.getPhone())
                                .isNotNull()
                                .isEqualTo(PHONE_NUMBER);

                        assertThat(updatedSeller.getAddresses())
                                .isNotEmpty()
                                .hasSize(1)
                                .satisfies(addresses -> assertThat(addresses.getFirst().getCity()).isEqualTo(CITY_NAME));
                    });
        }

        @Test
        @Order(3)
        void when_Non_Admin_Patches_Someone_Else_Profile_thenThrowsForbidden() {
            final String CITY_NAME = "New Put City";

            newAddress.setCity(CITY_NAME);

            Seller patchSeller = newSeller.deepClone();

            assertThatThrownBy(() -> patchSellerProfileById(sellerProfile.getId(), patchSeller)
                    .toEntity(Seller.class))
                    .isInstanceOf(HttpClientErrorException.Forbidden.class);
        }

        @Test
        @Order(4)
        void when_Non_Admin_Puts_Someone_Else_Profile_thenThrowsForbidden() {
            final String CITY_NAME = "New Patch City";

            newAddress.setCity(CITY_NAME);

            Seller putSeller = newSeller.deepClone();

            assertThatThrownBy(() -> putSellerById(sellerProfile.getId(), putSeller)
                    .toEntity(Seller.class))
                    .isInstanceOf(HttpClientErrorException.Forbidden.class);

        }

        @Test
        @Order(5)
        void when_Admin_Patches_Someone_Else_Profile_thenPatches() {

            userAuthFactory.loginAndReturnAdminJwt();
            instantiateClients();

            final String CITY_NAME = "New Patch City";

            newAddress.setCity(CITY_NAME);

            Seller patchSeller = newSeller.deepClone();

            patchSeller.setPhone(null);
            patchSeller.setAddresses(List.of(newAddress));

            ResponseEntity<Seller> putResponseEntity = patchSellerProfileById(newSeller.getId(), patchSeller)
                    .toEntity(Seller.class);

            assertThat(putResponseEntity)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull();
                        assertThat(response.getBody().getId())
                                .isNotNull()
                                .isEqualTo(newSeller.getId());
                    })
                    .extracting(ResponseEntity::getBody)
                    .satisfies(updatedSeller -> {

                        assertThat(updatedSeller.getPhone())
                                .isNotNull()
                                .isEqualTo(PHONE_NUMBER);

                        assertThat(updatedSeller.getAddresses())
                                .isNotEmpty()
                                .hasSize(1)
                                .satisfies(addresses -> assertThat(addresses.getFirst().getCity()).isEqualTo(CITY_NAME));
                    });
        }

        @Test
        @Order(6)
        void when_Admin_Puts_Someone_Else_Profile_thenPuts() {

            final String CITY_NAME = "New Put City";

            newAddress.setCity(CITY_NAME);

            Seller putSeller = newSeller.deepClone();

            putSeller.setPhone(null);
            putSeller.setAddresses(List.of(newAddress));

            ResponseEntity<Seller> putResponseEntity = putSellerById(newSeller.getId(), putSeller)
                    .toEntity(Seller.class);

            assertThat(putResponseEntity)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull();
                        assertThat(response.getBody().getId())
                                .isNotNull()
                                .isEqualTo(newSeller.getId());
                    })
                    .extracting(ResponseEntity::getBody)
                    .satisfies(updatedSeller -> {
                        assertThat(updatedSeller.getPhone())
                                .isNull();

                        assertThat(updatedSeller.getAddresses())
                                .isNotEmpty()
                                .hasSize(1)
                                .satisfies(addresses -> assertThat(addresses.getFirst().getCity()).isEqualTo(CITY_NAME));
                    });
        }
    }

    protected RestClient.@NotNull ResponseSpec getSellerProfileById(Long sellerId) {
        return sellerClient.get()
                .uri("/{id}", sellerId)
                .retrieve();
    }

    protected RestClient.@NotNull ResponseSpec putSellerById(Long sellerId, Seller updatedSeller) {
        return sellerClient.put()
                .uri("/{id}", sellerId)
                .body(updatedSeller)
                .retrieve();
    }

    protected RestClient.@NotNull ResponseSpec patchSellerProfileById(Long sellerId, Seller updatedSeller) {
        return sellerClient.patch()
                .uri("/{id}", sellerId)
                .body(updatedSeller)
                .retrieve();
    }

    protected RestClient.@NotNull ResponseSpec getAllSellerProfiles() {
        return sellerClient.get()
                .uri("/all")
                .retrieve();
    }

    protected void instantiateClients() {
        sellerClient = null;
        sellerClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("SELLER_API_PATH")));
    }
}