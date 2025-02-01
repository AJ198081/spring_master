package dev.aj.full_stack_v2.controllers;

import com.github.javafaker.Faker;
import dev.aj.full_stack_v2.PostgresTestContainerConfiguration;
import dev.aj.full_stack_v2.TestConfig;
import dev.aj.full_stack_v2.domain.entities.security.SecurityUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({PostgresTestContainerConfiguration.class, TestConfig.class})
@TestPropertySource(locations = "/application-test.properties", properties = {
        "logging.level.org.springframework.security=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AdminControllerTest {

    @Autowired
    private TestConfig testConfig;

    private RestClient restClient;

    @Autowired
    private Faker faker;

    @LocalServerPort
    private int port;

    @Value("${security.username}")
    private String securityUserName;

    @Value("${security.password}")
    private String securityPassword;

    @BeforeAll
    void beforeAll() {
        restClient = testConfig.restClient(port, "/api/admin");
    }

    @Test
    @Order(0)
    void getUsers() {
        ResponseEntity<List<SecurityUser>> usersResponse = restClient.get()
                .uri("/users")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(usersResponse.getBody());
        assertThat(usersResponse.getBody()).hasSizeGreaterThanOrEqualTo(1)
                .allMatch(user -> {
                    assertNotNull(user.getUsername());
                    assertNotNull(user.getId());
                    return true;
                });
    }

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    @Order(2)
    void updateUserRolePermittedToAdmin() {
        ResponseEntity<Void> roleUpdateResponse = restClient.put()
                .uri("/role?username=admin&role=ROLE_ADMIN")
                .header(HttpHeaders.AUTHORIZATION, "Basic %s".formatted(
                        Base64.getEncoder().encodeToString("%s:%s".formatted("AJ", securityPassword).getBytes())))
                .retrieve()
                .toBodilessEntity();

        Assertions.assertThat(roleUpdateResponse).isNotNull()
                .extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatusCode.valueOf(200));
    }

    @Test
    @Order(1)
    void updateUserRoleNotPermittedToUser() {

        ResponseEntity<List<SecurityUser>> usersResponse = restClient.get()
                .uri("/users")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        SecurityUser securityUser = Objects.requireNonNull(usersResponse.getBody())
                .stream()
                .filter(user -> user.getUsername().equals(securityUserName))
                .findFirst().orElseThrow();

        if (securityUser.getAuthorities().stream().anyMatch(x -> x.getAuthority().equalsIgnoreCase("ROLE_ADMIN"))) {
            restClient.put()
                    .uri("/role?username=admin&role=ROLE_USER")
                    .header(HttpHeaders.AUTHORIZATION, "Basic %s".formatted(
                            Base64.getEncoder().encodeToString("%s:%s".formatted(securityUserName, securityPassword).getBytes())))
                    .retrieve()
                    .toBodilessEntity();
        }

        RestClient.ResponseSpec responseSpec = restClient.put()
                .uri("/role?username=admin&role=ROLE_ADMIN")
                .header(HttpHeaders.AUTHORIZATION, "Basic %s".formatted(
                        Base64.getEncoder().encodeToString("%s:%s".formatted(securityUserName, securityPassword).getBytes())))
                .retrieve();

        HttpClientErrorException httpClientErrorException = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<Void> bodilessEntity = responseSpec.toBodilessEntity();
            HttpStatusCode statusCode = bodilessEntity.getStatusCode();
        });

        Assertions.assertThat(httpClientErrorException).isNotNull()
                .extracting(HttpClientErrorException::getStatusCode)
                .isEqualTo(HttpStatusCode.valueOf(403));
    }
}