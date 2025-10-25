package dev.aj.full_stack_v6_kafka.transfers.controllers;

import dev.aj.full_stack_v6_kafka.BootstrapTopics;
import dev.aj.full_stack_v6_kafka.TestConfig;
import dev.aj.full_stack_v6_kafka.TestDataFactory;
import dev.aj.full_stack_v6_kafka.common.domain.dtos.TransferRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, BootstrapTopics.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
@TestPropertySource(properties = {
        "junit.jupiter.execution.parallel.enabled=true"
})
@Execution(ExecutionMode.CONCURRENT)
class TransferControllerTest {

     public static final String BASE_URL_FORMAT = "http://localhost:%d%s";

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private Environment environment;

    private RestClient transferClient;

    @BeforeAll
    void setUp() {
        transferClient = testConfig.restClient(String.format(BASE_URL_FORMAT, port, environment.getRequiredProperty("TRANSFER_API_PATH")));
    }

    @AfterAll
    void tearDown() {
        transferClient = null;
    }

    @RepeatedTest(value = 1, name = "{displayName} : {currentRepetition}/{totalRepetitions}")
    void valid_Transfer_Processes_Successfully() {

        TransferRequestDto transferRequestDto = testDataFactory.getStreamOfTransferRequestDtos()
                .limit(1)
                .findFirst()
                .orElseThrow();

        ResponseEntity<Void> transferResponse = transferClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .queryParam("messageId", UUID.randomUUID().toString())
                        .build())
                .body(transferRequestDto)
                .retrieve()
                .toBodilessEntity();

        Assertions.assertThat(transferResponse)
                .isNotNull()
                .satisfies(response -> response.getStatusCode().is2xxSuccessful());
    }

    @RepeatedTest(value = 1, name = "{displayName} : {currentRepetition}/{totalRepetitions}")
    void inValid_transfer_Throws() {

        TransferRequestDto transferRequestDto = testDataFactory.getStreamOfTransferRequestDtos()
                .limit(1)
                .findFirst()
                .orElseThrow();

        RestClient.ResponseSpec transferResponseSpec = transferClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .build())
                .body(transferRequestDto)
                .retrieve();

        Assertions.assertThatThrownBy(transferResponseSpec::toBodilessEntity)
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
                .satisfies(status -> Assertions.assertThat(status)
                        .isEqualTo(HttpStatus.PRECONDITION_FAILED));
    }
}