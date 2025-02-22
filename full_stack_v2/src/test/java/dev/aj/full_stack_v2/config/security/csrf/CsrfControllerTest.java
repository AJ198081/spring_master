package dev.aj.full_stack_v2.config.security.csrf;

import dev.aj.full_stack_v2.PostgresTestContainerConfiguration;
import dev.aj.full_stack_v2.SecurityConfigForTesting;
import dev.aj.full_stack_v2.TestConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({PostgresTestContainerConfiguration.class, TestConfig.class, SecurityConfigForTesting.class,})
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class CsrfControllerTest {


    @LocalServerPort
    private int port;

    @Value("${security.username}")
    private String securityUsername;

    @Value("${security.password}")
    private String securityPassword;

    private RestClient restClient;

    @BeforeAll
    void setUp() {

        restClient = RestClient.builder()
                .baseUrl("http://localhost:%d/%s".formatted(port, "/api/csrf-token"))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

    }

    @Test
    void getCsrfToken() {

        ResponseEntity<DefaultCsrfToken> csrfTokenResponse = restClient.get()
                .retrieve()
                .toEntity(DefaultCsrfToken.class);

        assertThat(csrfTokenResponse.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());
        assertThat(csrfTokenResponse.getBody()).isNotNull()
                .satisfies(csrfToken -> {
                    assertThat(csrfToken.getHeaderName()).isEqualTo("X-XSRF-TOKEN");
                    assertThat(csrfToken.getParameterName()).isEqualTo("_csrf");
                    assertThat(csrfToken.getToken()).isNotBlank();
                });


    }
}