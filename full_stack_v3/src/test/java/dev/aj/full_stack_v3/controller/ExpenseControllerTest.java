package dev.aj.full_stack_v3.controller;

import dev.aj.full_stack_v3.PostgresTCConfig;
import dev.aj.full_stack_v3.TestConfig;
import dev.aj.full_stack_v3.TestData;
import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.domain.dto.ExpenseResponse;
import dev.aj.full_stack_v3.service.ExpenseService;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
@TestPropertySource(locations = "/application-test.properties", properties = {
        "logging.level.dev.aj.full_stack_v3.service.impl=debug"
})
@Import(value = {PostgresTCConfig.class, TestConfig.class, TestData.class})
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

    @BeforeAll
    void init() {
        restClient = testConfig.restClient("http://localhost:%d/%s".formatted(port, "api/v1/expenses"));

        List<ExpenseRequest> randomExpenseSamples = randomExpenses.limit(EXPENSE_SAMPLES_LIMIT)
                .toList();

        List<ExpenseResponse> savedExpenses = expenseService.saveExpenses(randomExpenseSamples);
        System.out.printf("Persisted %d expenses%n", savedExpenses.size());
    }


    @Test
    void getExpenses() {

        ResponseEntity<List<ExpenseResponse>> responseEntity = restClient.get()
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode()).is(new Condition<>(HttpStatusCode::is2xxSuccessful, "is 200 OK"));
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(responseEntity.getBody()).size()).isGreaterThanOrEqualTo(EXPENSE_SAMPLES_LIMIT);

    }

    @Test
    void saveExpenses() {
        ResponseEntity<List<ExpenseResponse>> responseEntity = restClient.post()
                .uri("/bulk")
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
                .body(expenseRequest)
                .retrieve();

        Assertions.assertThrows(HttpClientErrorException.class, () -> responseSpec.toEntity(ExpenseResponse.class));
    }

    @Test
    void expenseRequestWithValidDatePersistsSuccessfully() {
        ExpenseRequest expenseRequest = testData.getExpenseStream().findFirst().orElseThrow();

        expenseRequest.setDate(LocalDate.now().plusDays(1));

        ResponseEntity<ExpenseResponse> responseEntity = restClient.post()
                .body(expenseRequest)
                .retrieve()
                .toEntity(ExpenseResponse.class);

        assertThat(responseEntity.getStatusCode()).is(new Condition<>(HttpStatusCode::is2xxSuccessful, "is 200 OK"));
    }
}