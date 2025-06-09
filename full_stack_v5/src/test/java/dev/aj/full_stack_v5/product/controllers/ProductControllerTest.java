package dev.aj.full_stack_v5.product.controllers;

import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.product.domain.dtos.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ProductControllerTest {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));

        testDataFactory.generateStreamOfProducts().limit(10).forEach(product -> {
            ResponseEntity<ProductDto> productResponse = restClient.post()
                    .uri("/products")
                    .body(product)
                    .retrieve()
                    .toEntity(ProductDto.class);

            log.info("Added {} products", productResponse.getBody());
        });
    }

    @AfterAll
    void tearDown() {
        restClient = null;
    }

    @Test
    void addProduct() {

    }

    @Test
    void getAllProducts() {
    }

    @Test
    void getProductById() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProductById() {
    }

    @Test
    void getProductsByCategoryName() {
    }

    @Test
    void getProductsByBrand() {
    }

    @Test
    void getProductsByName() {
    }

    @Test
    void getProductsByCategoryNameAndBrand() {
    }

    @Test
    void getProductsByCategoryNameAndProductName() {
    }

    @Test
    void getProductsByBrandAndName() {
    }
}