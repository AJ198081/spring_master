package dev.aj.full_stack_v2.config.security.controller;

import com.github.javafaker.Faker;
import dev.aj.full_stack_v2.PostgresTestContainerConfiguration;
import dev.aj.full_stack_v2.TestConfig;
import dev.aj.full_stack_v2.domain.dto.LoginRequest;
import dev.aj.full_stack_v2.domain.dto.LoginResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({PostgresTestContainerConfiguration.class, TestConfig.class})
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

    @BeforeAll
    void beforeAll() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:%d/%s".formatted(port, "/api/auth/public"))
                .build();
    }

    @Test
    void throwsExceptionWhenCallingAuthenticateUser() {

        ResponseEntity<LoginResponse> loginResponse = restClient.post()
                .uri("/login")
                .body(LoginRequest.builder()
                        .username(securityUsername)
                        .password(securityPassword)
                        .build())
                .retrieve()
                .toEntity(LoginResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(ResponseEntity.status(200).build().getStatusCode());

    }
}