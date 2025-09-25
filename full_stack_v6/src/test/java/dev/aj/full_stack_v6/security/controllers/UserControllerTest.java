package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
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
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class UserControllerTest {

    public static final String USER_CONTROLLER_BASE_PATH = "/api/v1/users";

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserAuthFactory userAuthFactory;

    private RestClient restClient;

    private final Set<String> alreadySavedUsernames = new HashSet<>();

    @BeforeAll
    void setUp() {
        restClient = userAuthFactory.secureRestClient("http://localhost:%d%s".formatted(port, USER_CONTROLLER_BASE_PATH), port);
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
            UserCreateRequest userCreateRequest = getAUniqueUserCreateRequest();

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
        void whenDeletingOwnAccount_thenInternalServerError() {
            String currentUsername = userAuthFactory.currentUsername();
            if (currentUsername == null) {
                userAuthFactory.getBearerTokenHeader(port);
            }

            ResponseEntity<Void> deleteResponse = restClient.delete()
                    .uri("/%s".formatted(currentUsername))
                    .headers(headers -> headers.addAll(userAuthFactory.getBearerTokenHeader(port)))
                    .retrieve()
                    .toBodilessEntity();

            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        }
    }

    @Nested
    class updateUser {
        @Test
        void whenUpdatingOwnAccount_thenInternalServerError() {
            String currentUsername = userAuthFactory.getCurrentUsername(port);

            UserCreateRequest updateUserRequest = testDataFactory.userCreateRequest(currentUsername);

            ResponseEntity<Void> updateUserResponse = restClient.put()
                    .uri("/")
                    .headers(headers -> headers.addAll(userAuthFactory.getBearerTokenHeader(port)))
                    .body(updateUserRequest)
                    .retrieve()
                    .toBodilessEntity();

            assertThat(updateUserResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        }
    }

    @Nested
    class changePassword {
        @Test
        void whenChangingOwnPassword_thenInternalServerError() {
            String currentUsername = userAuthFactory.currentUsername();
            assertThat(currentUsername).isNotBlank();

            ResponseEntity<Void> changePasswordResult = restClient.patch()
                    .uri("/%s/password?password=%s".formatted(currentUsername, "newPassword123!"))
                    .retrieve()
                    .toBodilessEntity();

            assertThat(changePasswordResult.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        }
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