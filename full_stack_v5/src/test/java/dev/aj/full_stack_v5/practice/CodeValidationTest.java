package dev.aj.full_stack_v5.practice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest
@TestPropertySource(locations = {
        "/junit-platform.properties",
        "classpath:application-test.properties"
})
@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeValidationTest {

    @Autowired
    private CodeValidationService codeValidationService;

    RestClient restClient;

    @BeforeAll
    void beforeAll() {
        restClient = RestClient.builder()
                .baseUrl("https://www.skipapp.com.au/api/promotions")
                .build();
    }

    @RepeatedTest(value = 1000, name = "{currentRepetition}/{totalRepetitions}")
    void validateAndAddCodeToDatabase() throws InterruptedException {
        Thread.sleep(100);
        boolean isValid = codeValidationService.testCode(restClient);
        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void verifyUpdatePromotions() {
        codeValidationService.validateAndUpdatePromotions(restClient);
    }

}