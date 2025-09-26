package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AuthControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserAuthFactory userAuthFactory;

    private RestClient authClient;

    private UserCreateRequest userCreateRequest;

    @BeforeAll
    void setUp() {

        userAuthFactory.setClients(port);
        userCreateRequest = userAuthFactory.addANewUniqueUserWithAnyRole();
        authClient = userAuthFactory.getAuthClient();

        if (userCreateRequest == null) {
            throw new IllegalStateException("Failed to create a user for AuthControllerTest");
        }
    }

    @AfterAll
    void afterAll() {
        userAuthFactory.resetClients();
    }

    @Nested
    class TestLogin {
        @Test
        void whenValidCredentials_thenReturnsOkWithJwt() {

            LoginRequest loginRequest = new LoginRequest(userCreateRequest.username(), userCreateRequest.getPassword());

            ResponseEntity<LoginResponse> response = authClient.post()
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
                        assertThat(body.jwt())
                                .isNotBlank();
                        assertThat(body.username())
                                .isEqualTo(userCreateRequest.username());
                        assertThat(body.roles())
                                .isNotNull();
                    });
        }

        @Test
        void whenWrongPassword_thenUnauthorized() {
            LoginRequest badLogin = new LoginRequest(userCreateRequest.username(), userCreateRequest.getPassword() + "_wrong");

            Assertions.assertThatThrownBy(() -> authClient.post()
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
            ResponseEntity<Void> resp = authClient.post()
                    .uri("/logout")
                    .retrieve()
                    .toBodilessEntity();

            assertThat(resp)
                    .isNotNull()
                    .satisfies(entity -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));
        }
    }
}