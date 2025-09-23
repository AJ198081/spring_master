package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class})
@TestPropertySource(locations = {"/application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class UserControllerTest {

    public static final String USER_CONTROLLER_BASE_PATH = "/api/v1/users";

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    private RestClient restClient;

    private final Set<String> alreadySavedUsernames = new HashSet<>();

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d%s".formatted(port, USER_CONTROLLER_BASE_PATH));
    }

    @AfterAll
    void afterAll() {
        if (restClient != null) {
            restClient = null;
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class saveUserTests {

        @Order(1)
        @Test
        void whenValidUser_thenReturnsAccepted() {
            UserCreateRequest userCreateRequest = testDataFactory.getStreamOfUserRequests()
                    .filter(req -> alreadySavedUsernames.add(req.username()))
                    .findFirst()
                    .orElseThrow();

            ResponseEntity<Void> response = postANewUser(userCreateRequest);

            assertThat(response)
                    .isNotNull()
                    .satisfies(responseEntity -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));
        }

        @Order(2)
        @Test
        void whenDuplicateUsername_thenReturnsConflict() {
            UserCreateRequest userCreateRequestWithExistingUsername = testDataFactory.userCreateRequest(alreadySavedUsernames.stream().findFirst().orElseThrow());

            Assertions.assertThatThrownBy(
                            () -> restClient.post()
                                    .uri("/")
                                    .body(userCreateRequestWithExistingUsername)
                                    .retrieve()
                                    .toBodilessEntity()
                    )
                    .isInstanceOf(HttpClientErrorException.Conflict.class);
        }
    }

    @Nested
    class deleteUser {
        @Test
        void whenDeleteEndpointNotPresent_thenNotFound() {
            Assertions.assertThatThrownBy(() -> restClient.delete()
                            .uri("/nonexistent-user")
                            .retrieve()
                            .toBodilessEntity())
                    .isInstanceOf(HttpClientErrorException.NotFound.class);
        }
    }

    @Nested
    class updateUser {
        @Test
        void whenPutNotSupported_thenMethodNotAllowed() {
            UserCreateRequest req = testDataFactory.getStreamOfUserRequests()
                    .filter(r -> alreadySavedUsernames.add(r.username()))
                    .findFirst()
                    .orElseThrow();

            Assertions.assertThatThrownBy(() -> restClient.put()
                            .uri("/")
                            .body(req)
                            .retrieve()
                            .toBodilessEntity())
                    .isInstanceOf(HttpClientErrorException.MethodNotAllowed.class);
        }
    }

    @Nested
    class changePassword {
        @Test
        void whenPatchNotSupported_thenMethodNotAllowed() {
            UserCreateRequest req = testDataFactory.getStreamOfUserRequests()
                    .filter(r -> alreadySavedUsernames.add(r.username()))
                    .findFirst()
                    .orElseThrow();

            Assertions.assertThatThrownBy(() -> restClient.patch()
                            .uri("/")
                            .body(req)
                            .retrieve()
                            .toBodilessEntity())
                    .isInstanceOf(HttpClientErrorException.MethodNotAllowed.class);
        }
    }

    public UserCreateRequest addANewUniqueUser() {

        UserCreateRequest userCreateRequest = getAUniqueUserCreateRequest();

        ResponseEntity<Void> response = postANewUser(userCreateRequest);
        if (!response.getStatusCode().is2xxSuccessful()) {
            addANewUniqueUser();
        }
        return userCreateRequest;
    }

    private @NotNull ResponseEntity<Void> postANewUser(UserCreateRequest userCreateRequest) {

        return restClient.post()
                .uri("/")
                .body(userCreateRequest)
                .retrieve()
                .toBodilessEntity();
    }

    private @NotNull UserCreateRequest getAUniqueUserCreateRequest() {

        return testDataFactory.getStreamOfUserRequests()
                .filter(user -> !alreadySavedUsernames.contains(user.username()))
                .limit(1)
                .peek(req -> alreadySavedUsernames.add(req.username()))
                .findFirst()
                .orElseThrow();
    }
}