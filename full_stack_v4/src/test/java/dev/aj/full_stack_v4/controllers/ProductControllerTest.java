package dev.aj.full_stack_v4.controllers;

import dev.aj.full_stack_v4.PostgresTCConfig;
import dev.aj.full_stack_v4.TestConfig;
import dev.aj.full_stack_v4.TestData;
import dev.aj.full_stack_v4.domain.entities.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {PostgresTCConfig.class, TestData.class, TestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=update"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestData testData;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));
        testData.generateStreamOfProducts().limit(10).forEach(product -> {
            ResponseEntity<Product> productResponse = restClient.post()
                    .uri("/products")
                    .body(product)
                    .retrieve()
                    .toEntity(Product.class);
        });
    }

    @AfterAll
    void tearDown() {
        restClient = null;
    }

    @Test
    void getAll() {

        ResponseEntity<List<Product>> allProductsResponse = restClient.get()
                .uri("/products")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(allProductsResponse);
        Assertions.assertThat(allProductsResponse.getStatusCode().value()).isEqualTo(200);
        Assertions.assertThat(allProductsResponse.getBody()).hasSizeGreaterThanOrEqualTo(10);

    }

    @Test
    void create() {
        Product newProduct = testData.generateStreamOfProducts().limit(1).findFirst().orElseThrow();
        ResponseEntity<Product> createdProductResponse = restClient.post()
                .uri("/products")
                .body(newProduct)
                .retrieve()
                .toEntity(Product.class);

        assertNotNull(createdProductResponse);
        Assertions.assertThat(createdProductResponse.getStatusCode().value()).isEqualTo(201);
        assertEquals(newProduct.getName(), Objects.requireNonNull(createdProductResponse.getBody()).getName());
    }

    @Test
    void delete() {
        Product newProduct = testData.generateStreamOfProducts().limit(1).findFirst().orElseThrow();
        ResponseEntity<Product> createdProductResponse = restClient.post()
                .uri("/products")
                .body(newProduct)
                .retrieve()
                .toEntity(Product.class);

        ResponseEntity<Void> deletedProductResponseEntity = restClient.delete()
                .uri("/products/{id}", Objects.requireNonNull(createdProductResponse.getBody()).getId())
                .retrieve()
                .toBodilessEntity();

        assertNotNull(deletedProductResponseEntity);
        Assertions.assertThat(deletedProductResponseEntity.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void update() {
        Product newProduct = testData.generateStreamOfProducts().limit(1).findFirst().orElseThrow();
        ResponseEntity<Product> createdProductResponse = restClient.post()
                .uri("/products")
                .body(newProduct)
                .retrieve()
                .toEntity(Product.class);

        Product createdProduct = Objects.requireNonNull(createdProductResponse.getBody(), "Expected product to be created, but was empty");

        String suffixForUpdatedProductName = " - Updated";

        createdProduct.setName(newProduct.getName().concat(suffixForUpdatedProductName));

        ResponseEntity<Product> updatedProductResponseEntity = restClient.patch()
                .uri("/products/{id}", Objects.requireNonNull(createdProductResponse.getBody()).getId())
                .body(createdProduct)
                .retrieve()
                .toEntity(Product.class);

        assertNotNull(updatedProductResponseEntity);

        Assertions.assertThat(updatedProductResponseEntity.getStatusCode().value()).isEqualTo(200);

        Assertions.assertThat(Objects.requireNonNull(updatedProductResponseEntity.getBody()))
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

    }
}