package dev.aj.kafka.product.controllers;

import dev.aj.kafka.TestConfig;
import dev.aj.kafka.TestData;
import dev.aj.kafka.product.domain.dto.ProductCreateDto;
import dev.aj.kafka.product.domain.dto.ProductCreatedDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.Objects;

import static java.time.Duration.ofSeconds;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestData.class})
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ProductControllerTest {

    @Autowired
    private TestData testData;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private Environment environment;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d/products".formatted(port));
    }

    @AfterAll
    void tearDown() {
        restClient.delete()
                .uri("/all")
                .retrieve()
                .toBodilessEntity();
    }

    @Test
    void createProduct() {
        testData.streamOfCreateProductDtos()
                .limit(5)
                .peek(this::introduceDelayInProductCreation)
                .forEach(productCreateDto -> {
                    ResponseEntity<ProductCreatedDto> productCreatedResponse = restClient.post()
                            .body(productCreateDto)
                            .retrieve()
                            .toEntity(new ParameterizedTypeReference<>() {
                            });

                    org.assertj.core.api.Assertions.assertThat(productCreatedResponse).isNotNull()
                            .extracting("status", "body.id")
                            .containsExactly(HttpStatus.CREATED, Objects.requireNonNull(productCreatedResponse.getBody()).getId());
                });
    }

    private void introduceDelayInProductCreation(ProductCreateDto productCreateDto) {
        try {
            Long delayInSeconds = environment.getProperty("delay.production.creation.seconds", Long.class, 100L);
            Thread.sleep(ofSeconds(delayInSeconds).toMillis());
            log.info("Delay of {} seconds introduced for product creation", delayInSeconds);
            log.info("Product created: {}", productCreateDto.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}