package dev.aj.redisson.controllers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FibonacciControllerTest {

    private RestClient restClient;
    private static final String BASE_URL = "http://localhost:%d";

    @LocalServerPort
    private int port;


    @BeforeAll
    void beforeAll() {
        restClient = RestClient.builder()
                .baseUrl(String.format(BASE_URL, port))
                .build();
    }

    @Test
    @Order(1)
    void getFibonacciNumber() {
        ResponseEntity<Long> fibonacciNumberResponse = restClient.get()
                .uri("/fibonacci/10")
                .retrieve()
                .toEntity(Long.class);

        Assertions.assertThat(fibonacciNumberResponse).isNotNull();
        Assertions.assertThat(fibonacciNumberResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(fibonacciNumberResponse.getBody()).isEqualTo(55L);
    }

    @Test
    @Order(2)
    void getFibonacciForALargeNumber() {
        ResponseEntity<Long> fibonacciNumberResponse = restClient.get()
                .uri("/fibonacci/50")
                .retrieve()
                .toEntity(Long.class);

        Assertions.assertThat(fibonacciNumberResponse).isNotNull();
        Assertions.assertThat(fibonacciNumberResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(fibonacciNumberResponse.getBody()).isEqualTo(12586269025L);
    }

    @Test
    @Order(3)
    void getFibonacciForCoupleOfLargeNumbers() {

        ResponseEntity<Long> fibonacciNumberResponse50 = restClient.get()
                .uri("/fibonacci/45")
                .retrieve()
                .toEntity(Long.class);

        ResponseEntity<Long> fibonacciNumberResponse55 = restClient.get()
                .uri("/fibonacci/50")
                .retrieve()
                .toEntity(Long.class);
    }
}