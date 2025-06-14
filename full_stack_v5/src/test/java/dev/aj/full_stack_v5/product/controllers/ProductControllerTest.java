package dev.aj.full_stack_v5.product.controllers;

import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.product.domain.dtos.ProductRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class})
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

        testDataFactory.generateStreamOfProductRequests()
                .limit(10)
                .forEach(product -> {
                    ResponseEntity<ProductResponseDto> productResponse = restClient.post()
                            .uri("/api/v1/products/")
                            .body(product)
                            .retrieve()
                            .toEntity(ProductResponseDto.class);

                    log.info("Added {} products", productResponse.getBody());
                });
    }

    @AfterAll
    void tearDown() {
        restClient = null;
    }

    @Test
    void addProduct() {
        ProductRequestDto productRequest = testDataFactory.generateStreamOfProductRequests()
                .findFirst()
                .orElseThrow();

        ResponseEntity<ProductResponseDto> response = restClient.post()
                .uri("/api/v1/products/")
                .body(productRequest)
                .retrieve()
                .toEntity(ProductResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(productRequest.getName(), response.getBody().getName());
        Assertions.assertEquals(productRequest.getBrand(), response.getBody().getBrand());
        Assertions.assertEquals(productRequest.getDescription(), response.getBody().getDescription());
        Assertions.assertEquals(productRequest.getPrice(), response.getBody().getPrice());
        Assertions.assertEquals(productRequest.getInventory(), response.getBody().getInventory());
        Assertions.assertEquals(productRequest.getCategoryName(), response.getBody().getCategoryName());
    }

    @Test
    void throwsIllegalArgsExceptionWhenSavingDuplicatedProduct() {
        ProductRequestDto productRequest = testDataFactory.generateStreamOfProductRequests()
                .findFirst()
                .orElseThrow();

        RestClient.RequestBodySpec addProductRequest = restClient.post()
                .uri("/api/v1/products/")
                .body(productRequest);

        ResponseEntity<Void> addProductResponse = addProductRequest
                .retrieve()
                .toBodilessEntity();

        Assertions.assertEquals(HttpStatus.CREATED, addProductResponse.getStatusCode());

        Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class,
                () -> addProductRequest.retrieve().toEntity(ProductResponseDto.class)
        );


    }

    @Test
    void getAllProducts() {
        ResponseEntity<List<ProductResponseDto>> response = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());

        // We added 10 products in setup, plus one in the addProduct test
        Assertions.assertTrue(response.getBody().size() >= 10);
    }

    @Test
    void getProductById() {
        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, allProductsResponse.getStatusCode());
        Assertions.assertNotNull(allProductsResponse.getBody());
        Assertions.assertFalse(allProductsResponse.getBody().isEmpty());
        org.assertj.core.api.Assertions.assertThat(allProductsResponse.getBody().size()).isGreaterThanOrEqualTo(10);

        ProductResponseDto firstProduct = allProductsResponse.getBody().getFirst();
        Long productId = firstProduct.getId();

        // Now get this product by ID
        ResponseEntity<ProductResponseDto> productResponse = restClient.get()
                .uri("/api/v1/products/{id}", productId)
                .retrieve()
                .toEntity(ProductResponseDto.class);

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, productResponse.getStatusCode());
        Assertions.assertNotNull(productResponse.getBody());
        Assertions.assertEquals(productId, productResponse.getBody().getId());
        Assertions.assertEquals(firstProduct.getName(), productResponse.getBody().getName());
        Assertions.assertEquals(firstProduct.getBrand(), productResponse.getBody().getBrand());
    }

    @Test
    void updateProduct() {
        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, allProductsResponse.getStatusCode());
        Assertions.assertNotNull(allProductsResponse.getBody());
        Assertions.assertFalse(allProductsResponse.getBody().isEmpty());
        org.assertj.core.api.Assertions.assertThat(allProductsResponse.getBody().size()).isGreaterThanOrEqualTo(10);

        ProductResponseDto lastProduct = allProductsResponse.getBody().getLast();
        Long productId = lastProduct.getId();

        // Create an updated product request, we are going to replace the product with a new product, it is Put, and not Patch
        ProductRequestDto updatedProduct = testDataFactory.generateStreamOfProductRequests()
                .findFirst()
                .orElseThrow();

        ResponseEntity<ProductResponseDto> productUpdateResponse = restClient.put()
                .uri("/api/v1/products/{id}", productId)
                .body(updatedProduct)
                .retrieve()
                .toEntity(ProductResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, productUpdateResponse.getStatusCode());
        Assertions.assertNotNull(productUpdateResponse.getBody());
        Assertions.assertEquals(productId, productUpdateResponse.getBody().getId());
        Assertions.assertEquals(updatedProduct.getName(), productUpdateResponse.getBody().getName());
        Assertions.assertEquals(updatedProduct.getBrand(), productUpdateResponse.getBody().getBrand());
        Assertions.assertEquals(updatedProduct.getDescription(), productUpdateResponse.getBody().getDescription());
        Assertions.assertEquals(updatedProduct.getPrice(), productUpdateResponse.getBody().getPrice());
        Assertions.assertEquals(updatedProduct.getInventory(), productUpdateResponse.getBody().getInventory());
        Assertions.assertEquals(updatedProduct.getCategoryName(), productUpdateResponse.getBody().getCategoryName());
    }

    @Test
    void deleteProductById() {
        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allProductsResponse.getBody());
        ProductResponseDto firstProduct = allProductsResponse.getBody().getFirst();
        Long productId = firstProduct.getId();

        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/api/v1/products/{id}", productId)
                .retrieve()
                .toEntity(Void.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Try to retrieve the same productId, should throw no found
        RestClient.ResponseSpec responseSpec = restClient.get()
                .uri("/api/v1/products/{id}", productId)
                .retrieve();

        Assertions.assertThrows(Exception.class, () -> responseSpec.toEntity(ProductResponseDto.class));
    }

    @Test
    void getProductsByCategoryName() {

        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allProductsResponse.getBody());
        Assertions.assertFalse(allProductsResponse.getBody().isEmpty());

        ProductResponseDto firstProduct = allProductsResponse.getBody().getFirst();
        String categoryName = firstProduct.getCategoryName();

        // Send GET request to get products by category name
        ResponseEntity<List<ProductResponseDto>> categoryResponse = restClient.get()
                .uri("/api/v1/products/category/{categoryName}", categoryName)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
        Assertions.assertNotNull(categoryResponse.getBody());
        Assertions.assertFalse(categoryResponse.getBody().isEmpty());

        for (ProductResponseDto product : categoryResponse.getBody()) {
            Assertions.assertEquals(categoryName, product.getCategoryName());
        }
    }

    @Test
    void getProductsByBrand() {
        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allProductsResponse.getBody());
        ProductResponseDto firstProduct = allProductsResponse.getBody().getFirst();
        String brand = firstProduct.getBrand();

        ResponseEntity<List<ProductResponseDto>> brandResponse = restClient.get()
                .uri("/api/v1/products/brand/{brandName}", brand)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, brandResponse.getStatusCode());
        Assertions.assertNotNull(brandResponse.getBody());
        Assertions.assertFalse(brandResponse.getBody().isEmpty());

        for (ProductResponseDto product : brandResponse.getBody()) {
            Assertions.assertEquals(brand, product.getBrand());
        }
    }

    @Test
    void getProductsByName() {
        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allProductsResponse.getBody());
        ProductResponseDto firstProduct = allProductsResponse.getBody().getFirst();
        String name = firstProduct.getName();

        ResponseEntity<List<ProductResponseDto>> nameResponse = restClient.get()
                .uri("/api/v1/products/name/{productName}", name)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, nameResponse.getStatusCode());
        Assertions.assertNotNull(nameResponse.getBody());
        Assertions.assertFalse(nameResponse.getBody().isEmpty());

        for (ProductResponseDto product : nameResponse.getBody()) {
            Assertions.assertEquals(name, product.getName());
        }
    }

    @Test
    void getProductsByCategoryNameAndBrand() {
        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allProductsResponse.getBody());
        ProductResponseDto firstProduct = allProductsResponse.getBody().getFirst();
        String categoryName = firstProduct.getCategoryName();
        String brand = firstProduct.getBrand();

        ResponseEntity<List<ProductResponseDto>> categoryBrandResponse = restClient.get()
                .uri("/api/v1/products/category/{categoryName}/brand/{brandName}", categoryName, brand)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, categoryBrandResponse.getStatusCode());
        Assertions.assertNotNull(categoryBrandResponse.getBody());

        if (!categoryBrandResponse.getBody().isEmpty()) {
            for (ProductResponseDto product : categoryBrandResponse.getBody()) {
                Assertions.assertEquals(categoryName, product.getCategoryName());
                Assertions.assertEquals(brand, product.getBrand());
            }
        }
    }

    @Test
    void getProductsByCategoryNameAndProductName() {
        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allProductsResponse.getBody());
        ProductResponseDto firstProduct = allProductsResponse.getBody().getFirst();
        String categoryName = firstProduct.getCategoryName();
        String productName = firstProduct.getName();

        ResponseEntity<List<ProductResponseDto>> categoryNameResponse = restClient.get()
                .uri("/api/v1/products/category/{categoryName}/name/{productName}", categoryName, productName)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, categoryNameResponse.getStatusCode());
        Assertions.assertNotNull(categoryNameResponse.getBody());

        if (!categoryNameResponse.getBody().isEmpty()) {
            for (ProductResponseDto product : categoryNameResponse.getBody()) {
                Assertions.assertEquals(categoryName, product.getCategoryName());
                Assertions.assertEquals(productName, product.getName());
            }
        }
    }

    @Test
    void getProductsByBrandAndName() {
        ResponseEntity<List<ProductResponseDto>> allProductsResponse = restClient.get()
                .uri("/api/v1/products/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allProductsResponse.getBody());
        ProductResponseDto firstProduct = allProductsResponse.getBody().getFirst();
        String brand = firstProduct.getBrand();
        String productName = firstProduct.getName();

        ResponseEntity<List<ProductResponseDto>> brandNameResponse = restClient.get()
                .uri("/api/v1/products/brand/{brandName}/product/{productName}", brand, productName)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, brandNameResponse.getStatusCode());
        Assertions.assertNotNull(brandNameResponse.getBody());

        if (!brandNameResponse.getBody().isEmpty()) {
            for (ProductResponseDto product : brandNameResponse.getBody()) {
                Assertions.assertEquals(brand, product.getBrand());
                Assertions.assertEquals(productName, product.getName());
            }
        }
    }
}
