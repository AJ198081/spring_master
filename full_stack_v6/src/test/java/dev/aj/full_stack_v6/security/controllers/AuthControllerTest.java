package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.common.domain.dtos.LoginRequest;
import dev.aj.full_stack_v6.common.domain.dtos.LoginResponse;
import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static dev.aj.full_stack_v6.security.controllers.UserControllerTest.USER_CONTROLLER_BASE_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class})
@TestPropertySource(locations = {"/application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AuthControllerTest {

    public static final String AUTH_CONTROLLER_BASE_PATH = "/api/v1/auths";

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    private RestClient restClient;
    private UserCreateRequest userCreateRequest;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d%s".formatted(port, AUTH_CONTROLLER_BASE_PATH));
        RestClient userClient = testConfig.restClient("http://localhost:%d%s".formatted(port, USER_CONTROLLER_BASE_PATH));

        // Try a few times in case of accidental username collision
        for (int i = 0; i < 3; i++) {
            UserCreateRequest candidate = testDataFactory.getStreamOfUserRequests().findFirst().orElseThrow();
            try {
                userClient.post()
                        .uri("/")
                        .body(candidate)
                        .retrieve()
                        .toBodilessEntity();
                userCreateRequest = candidate;
                break;
            } catch (HttpClientErrorException.Conflict e) {
                // try another username
            }
        }
        if (userCreateRequest == null) {
            throw new IllegalStateException("Failed to create a user for AuthControllerTest");
        }
    }

    @AfterAll
    void afterAll() {
        if (restClient != null) {
            restClient = null;
        }
    }

    @Nested
    class TestLogin {
        @Test
        void whenValidCredentials_thenReturnsOkWithJwt() {
            // Use the created user's credentials to login
            LoginRequest loginRequest = new LoginRequest(userCreateRequest.username(), userCreateRequest.getPassword());

            ResponseEntity<LoginResponse> response = restClient.post()
                    .uri("/login")
                    .body(loginRequest)
                    .retrieve()
                    .toEntity(LoginResponse.class);

            assertThat(response)
                    .isNotNull()
                    .satisfies(resp -> assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK))
                    .extracting(ResponseEntity::getBody)
                    .isNotNull()
                    .satisfies(body -> {
                        assertThat(body.jwt()).isNotBlank();
                        assertThat(body.username()).isEqualTo(userCreateRequest.username());
                        assertThat(body.roles())
                                 .isNotNull();
                    });
        }

        @Test
        void whenWrongPassword_thenUnauthorized() {
            LoginRequest badLogin = new LoginRequest(userCreateRequest.username(), userCreateRequest.getPassword() + "_wrong");

            Assertions.assertThatThrownBy(() -> restClient.post()
                            .uri("/login")
                            .body(badLogin)
                            .retrieve()
                            .toEntity(LoginResponse.class))
                    .isInstanceOf(HttpClientErrorException.Unauthorized.class);
        }
    }

    @Nested
    class TestLogout {
        @Test
        void whenLogout_thenAccepted() {
            ResponseEntity<Void> resp = restClient.post()
                    .uri("/logout")
                    .retrieve()
                    .toBodilessEntity();

            assertThat(resp)
                    .isNotNull()
                    .satisfies(entity -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));
        }
    }
}