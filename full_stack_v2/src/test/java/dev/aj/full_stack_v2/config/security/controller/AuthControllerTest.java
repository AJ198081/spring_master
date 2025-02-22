package dev.aj.full_stack_v2.config.security.controller;

import com.github.javafaker.Faker;
import dev.aj.full_stack_v2.PostgresTestContainerConfiguration;
import dev.aj.full_stack_v2.SecurityConfigForTesting;
import dev.aj.full_stack_v2.TestConfig;
import dev.aj.full_stack_v2.domain.dto.LoginRequest;
import dev.aj.full_stack_v2.domain.dto.LoginResponse;
import dev.aj.full_stack_v2.domain.entities.security.SecurityUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({PostgresTestContainerConfiguration.class, TestConfig.class, SecurityConfigForTesting.class,})
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Value("${security.username}")
    private String securityUsername;

    @Value("${security.password}")
    private String securityPassword;

    private RestClient restClient;

    private String jwtToken;

    @Autowired
    private Faker faker;

    private SecurityUser newUser;

    private String newUserJwtToken;

    @BeforeAll
    void beforeAll() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:%d/%s".formatted(port, "/api/auth/public"))
                .build();
    }

    @Test
    @Order(0)
    void successfullyAuthenticatesUserAndGetsJwtToken() {
        ResponseEntity<LoginResponse> loginResponse = restClient.post()
                .uri("/login")
                .body(LoginRequest.builder()
                        .username(securityUsername)
                        .password(securityPassword)
                        .build())
                .retrieve()
                .toEntity(LoginResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(ResponseEntity.status(200).build().getStatusCode());
        assertThat(loginResponse.getBody()).isNotNull()
                .satisfies(response -> {
                    assertThat(response.username()).isEqualTo(securityUsername);
                    assertThat(response.jwtToken()).isNotBlank();
                    assertThat(response.jwtToken().split("\\.").length).isEqualTo(3);
                    assertThat(response.roles()).containsExactly("ROLE_USER");
                });
        jwtToken = loginResponse.getBody().jwtToken();
    }

    @Test
    @Order(1)
    void authenticateWithJwtToken() {
        RestClient authenticatedRestClient = RestClient.builder()
                .baseUrl("http://localhost:%d/%s".formatted(port, "/api/admin"))
                .defaultHeader("Authorization", "Bearer %s".formatted(jwtToken))
                .build();

        ResponseEntity<List<SecurityUser>> authenticatedResponse = authenticatedRestClient.get()
                .uri("/users")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertThat(authenticatedResponse.getStatusCode()).isEqualTo(ResponseEntity.status(200).build().getStatusCode());
        assertThat(authenticatedResponse.getBody()).isNotNull();
        assertThat(authenticatedResponse.getBody()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(0)
    void registerUser() {

        newUser = SecurityUser.builder()
                .username(faker.name().username())
                .password(faker.internet().password())
                .authorities(Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
                .build();

        RestClient signupRestClient = RestClient.builder()
                .baseUrl("http://localhost:%d/%s".formatted(port, "/api/auth/public"))
                .build();

        ResponseEntity<String> registrationResponse = signupRestClient.post()
                .uri("/signup")
                .body(newUser)
                .retrieve()
                .toEntity(String.class);

        assertThat(registrationResponse.getStatusCode()).isEqualTo(ResponseEntity.status(201).build().getStatusCode());
        assertThat(registrationResponse.getBody()).isNotNull();
        assertThat(registrationResponse.getBody()).contains(newUser.getUsername());
    }

    @Test
    @Order(2)
    void authenticatesUserAndGetsJwtToken() {
        RestClient jwtRestClient = RestClient.builder()
                .baseUrl("http://localhost:%d/%s".formatted(port, "/api/auth/public"))
                .build();

        ResponseEntity<LoginResponse> loginResponse = jwtRestClient
                .post()
                .uri("/login")
                .body(LoginRequest.builder()
                        .username(newUser.getUsername())
                        .password(newUser.getPassword())
                        .build())
                .retrieve()
                .toEntity(LoginResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(ResponseEntity.status(200).build().getStatusCode());
        assertThat(loginResponse.getBody()).isNotNull()
                .satisfies(response -> {
                    assertThat(response.username()).isEqualTo(newUser.getUsername());
                    assertThat(response.jwtToken()).isNotBlank();
                    assertThat(response.jwtToken().split("\\.").length).isEqualTo(3);
//                    assertThat(response.roles()).containsExactly(newUser.getAuthorities().toArray());
                });

        newUserJwtToken = loginResponse.getBody().jwtToken();
    }

    @Test
    void getAllUsersUsingJwtToken() {
        RestClient authenticatedRestClient = RestClient.builder()
                .baseUrl("http://localhost:%d/%s".formatted(port, "/api/auth/public"))
                .defaultHeader("Authorization", "Bearer %s".formatted(newUserJwtToken))
                .build();

        ResponseEntity<List<SecurityUser>> authenticatedResponse = authenticatedRestClient.get()
                .uri("/registered_users")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertThat(authenticatedResponse.getStatusCode()).isEqualTo(ResponseEntity.status(200).build().getStatusCode());
        assertThat(authenticatedResponse.getBody()).isNotNull();
        assertThat(authenticatedResponse.getBody()).hasSizeGreaterThan(2);
    }
}
