package dev.aj.full_stack_v5.practice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
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

    @RepeatedTest(2)
    void verifyTestCodeMethodValidatesGiveCode() throws InterruptedException {
        Thread.sleep(100);
        boolean isValid = codeValidationService.testCode(restClient);
        Assertions.assertThat(isValid).isTrue();
    }
}