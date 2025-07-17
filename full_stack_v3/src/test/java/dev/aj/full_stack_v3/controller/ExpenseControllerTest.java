package dev.aj.full_stack_v3.controller;

import dev.aj.full_stack_v3.TestConfig;
import dev.aj.full_stack_v3.TestData;
import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.domain.dto.ExpenseResponse;
import dev.aj.full_stack_v3.domain.dto.UserLoginRequest;
import dev.aj.full_stack_v3.domain.dto.UserLoginResponse;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationResponse;
import dev.aj.full_stack_v3.service.ExpenseService;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "/application-compose.properties", properties = {
        "logging.level.dev.aj.full_stack_v3.service.impl=debug"
})
@Import(value = {TestConfig.class, TestData.class})
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class ExpenseControllerTest {

    public static final int EXPENSE_SAMPLES_LIMIT = 10;

    @LocalServerPort
    private int port;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private TestData testData;

    @Autowired
    private Stream<ExpenseRequest> randomExpenses;

    private RestClient restClient;

    private String jwtToken;

    @BeforeAll
    void init() {

        RestClient authRestClient = testConfig.restClient("http://localhost:%d/%s".formatted(port, "api/v1/auth"));

        UserRegistrationRequest userRegistrationRequest = testData.getUserRegistrationRequestStream()
                .limit(1)
                .findFirst()
                .orElseThrow();

        UserRegistrationResponse responseEntity = authRestClient.post()
                .uri("/register")
                .body(userRegistrationRequest)
                .retrieve()
                .toEntity(UserRegistrationResponse.class).getBody();

        assert responseEntity != null;

        ResponseEntity<UserLoginResponse> userLoginResponse = authRestClient
                .post()
                .uri("/login")
                .body(UserLoginRequest.builder()
                        .username(responseEntity.getUsername())
                        .password(userRegistrationRequest.getPassword())
                        .build())
                .retrieve()
                .toEntity(UserLoginResponse.class);

        jwtToken = Objects.requireNonNull(userLoginResponse.getBody()).getToken();


        restClient = testConfig.restClient("http://localhost:%d/%s".formatted(port, "api/v1/expenses"));

        List<ExpenseRequest> randomExpenseSamples = randomExpenses.limit(EXPENSE_SAMPLES_LIMIT)
                .toList();

        List<ExpenseResponse> savedExpenses = expenseService.saveExpenses(randomExpenseSamples);
        System.out.printf("Persisted %d expenses%n", savedExpenses.size());
    }


    @Test
    void getExpenses() {

        ResponseEntity<List<ExpenseResponse>> responseEntity = restClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwtToken))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode()).is(new Condition<>(HttpStatusCode::is2xxSuccessful, "is 200 OK"));
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(responseEntity.getBody()).size()).isGreaterThanOrEqualTo(EXPENSE_SAMPLES_LIMIT);

    }

    @Test
    @WithMockUser(username = "testuser", password = "<PASSWORD>")
    void saveExpenses() {
        ResponseEntity<List<ExpenseResponse>> responseEntity = restClient.post()
                .uri("/bulk")
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwtToken))
                .body(testData.getExpenseStream().limit(2).toList())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode()).is(new Condition<>(HttpStatusCode::is2xxSuccessful, "is 200 OK"));
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(responseEntity.getBody()).size()).isEqualTo(2);
    }

    @Test
    void expenseRequestWithInvalidDateThrowsException() {
        ExpenseRequest expenseRequest = testData.getExpenseStream().findFirst().orElseThrow();

        expenseRequest.setDate(LocalDate.now().plusYears(1).plusDays(1));

         var responseSpec = restClient.post()
                 .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwtToken))
                .body(expenseRequest)
                .retrieve();

        Assertions.assertThrows(HttpClientErrorException.class, () -> responseSpec.toEntity(ExpenseResponse.class));
    }

    @Test
    void expenseRequestWithValidDatePersistsSuccessfully() {
        ExpenseRequest expenseRequest = testData.getExpenseStream().findFirst().orElseThrow();

        expenseRequest.setDate(LocalDate.now().plusYears(1).minusDays(1));

        ResponseEntity<ExpenseResponse> responseEntity = restClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwtToken))
                .body(expenseRequest)
                .retrieve()
                .toEntity(ExpenseResponse.class);

        assertThat(responseEntity.getStatusCode()).is(new Condition<>(HttpStatusCode::is2xxSuccessful, "is 200 OK"));
    }
}