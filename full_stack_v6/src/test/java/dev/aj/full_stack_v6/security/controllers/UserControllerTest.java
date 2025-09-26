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
import org.junit.jupiter.api.RepeatedTest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    private RestClient authenticatedRestClient;

    @BeforeAll
    void setUp() {
        userAuthFactory.setClients(port);
        authenticatedRestClient = userAuthFactory.secureRestClient("http://localhost:%d%s".formatted(port, USER_CONTROLLER_BASE_PATH));
    }

    @AfterAll
    void afterAll() {
        if (authenticatedRestClient != null) {
            authenticatedRestClient = null;
        }
        userAuthFactory.resetClients();
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class saveUserTests {

        private String persistedUsername;

        @Order(1)
        @Test
        void whenValidUser_thenReturnsAccepted() {
            UserCreateRequest userCreateRequest = userAuthFactory.getAUniqueUserCreateRequest();

            ResponseEntity<Void> response = postANewUser(userCreateRequest);

            assertThat(response)
                    .isNotNull()
                    .satisfies(responseEntity -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));

            persistedUsername = userCreateRequest.username();
        }

        @Order(2)
        @Test
        void whenDuplicateUsername_thenReturnsConflict() {
            UserCreateRequest userCreateRequestWithExistingUsername = testDataFactory.userCreateRequest(persistedUsername);

            Assertions.assertThatThrownBy(
                            () -> authenticatedRestClient.post()
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
        void whenDeletingOwnAccount_thenAccepted() {
            String currentUsername = userAuthFactory.currentUsername();
            if (currentUsername == null) {
                userAuthFactory.getBearerTokenHeader();
            }

            ResponseEntity<Void> deleteResponse = authenticatedRestClient.delete()
                    .uri("/%s".formatted(currentUsername))
                    .headers(headers -> headers.addAll(userAuthFactory.getBearerTokenHeader()))
                    .retrieve()
                    .toBodilessEntity();
            userAuthFactory.deleteCurrentUser();
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        }

        @RepeatedTest(value = 1)
        void whenDeletingSomeoneElseAccount_andNotAdmin_thenThrowsNotAcceptableException() {

            UserCreateRequest otherUser = userAuthFactory.addANewUniqueUserWithAnyRole();

            userAuthFactory.loginAsDifferentNewUser(
                    UserAuthFactory.ROLE_USER,
                    otherUser.username()
            );

            assertThatThrownBy(() -> authenticatedRestClient.delete()
                    .uri("/%s".formatted(otherUser.username()))
                    .headers(headers -> headers.addAll(userAuthFactory.getBearerTokenHeader()))
                    .retrieve()
                    .toBodilessEntity())
                    .isInstanceOf(HttpClientErrorException.NotAcceptable.class);
        }

        @Test
        void whenDeletingSomeoneElseAccount_andIsAdmin_thenAccepted() {

            UserCreateRequest otherUser = userAuthFactory.addANewUniqueUserWithAnyRole();

            String currentUsername = userAuthFactory.loginAndReturnAdminUsername();

            ResponseEntity<Void> deleteUserResponse = authenticatedRestClient.delete()
                    .uri("/%s".formatted(otherUser.username()))
                    .headers(headers -> headers.addAll(userAuthFactory.getBearerTokenHeader()))
                    .retrieve()
                    .toBodilessEntity();

            userAuthFactory.deleteCurrentUser();
            assertThat(deleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        }
    }

    @Nested
    class updateUser {
        @Test
        void whenUpdatingOwnAccount_thenAccepted() {

            String currentUsername = userAuthFactory.getCurrentUsername();

            UserCreateRequest updateUserRequest = testDataFactory.userCreateRequest(currentUsername);

            ResponseEntity<Void> updateUserResponse = authenticatedRestClient.put()
                    .uri("/")
                    .headers(headers -> headers.addAll(userAuthFactory.getBearerTokenHeader()))
                    .body(updateUserRequest)
                    .retrieve()
                    .toBodilessEntity();

            userAuthFactory.deleteCurrentUser();

            assertThat(updateUserResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        }
    }

    @Nested
    class changePassword {
        @Test
        void whenChangingOwnPassword_thenAccepted() {
            String currentUsername = userAuthFactory.currentUsername();
            assertThat(currentUsername).isNotBlank();

            ResponseEntity<Void> changePasswordResult = authenticatedRestClient.patch()
                    .uri("/%s/password?password=%s".formatted(currentUsername, "newPassword123!"))
                    .retrieve()
                    .toBodilessEntity();

            userAuthFactory.deleteCurrentUser();

            assertThat(changePasswordResult.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        }
    }

    private @NotNull ResponseEntity<Void> postANewUser(UserCreateRequest userCreateRequest) {

        return authenticatedRestClient.post()
                .uri("/")
                .body(userCreateRequest)
                .retrieve()
                .toBodilessEntity();
    }
}