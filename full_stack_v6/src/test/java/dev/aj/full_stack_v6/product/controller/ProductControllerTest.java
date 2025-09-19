package dev.aj.full_stack_v6.product.controller;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.common.domain.dtos.PageResponse;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.product.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class})
@TestPropertySource(locations = {"classpath:application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ProductControllerTest {

    private static final String PRODUCT_CONTROLLER_BASE_PATH = "/api/v1/products";

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ProductRepository productRepository;

    private RestClient restClient;

    private Set<String> alreadyCommittedProductNames = new HashSet<>();

    @BeforeEach
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d%s".formatted(port, PRODUCT_CONTROLLER_BASE_PATH));
        productRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        if (restClient != null) {
            restClient = null;
        }
    }

    @Nested
    class PostProductTests {

        @Test
        void whenValidProduct_thenCreatesProduct() {
            Product newProduct = createSampleProduct();
            ResponseEntity<Product> createdProductResponse = saveANewRandomProduct(newProduct);

            Assertions.assertThat(createdProductResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(product -> Assertions.assertThat(product.getName())
                                        .isNotNull()
                                        .isEqualTo(newProduct.getName()));
                    });
        }

        @Test
        void whenDuplicateName_thenReturnsConflict() {
            Product newProduct = createSampleProduct();
            saveANewRandomProduct(newProduct);

            Assertions.assertThatThrownBy(() -> saveANewRandomProduct(newProduct))
                    .isInstanceOf(HttpClientErrorException.Conflict.class);
        }
    }

    @Nested
    class GetProductTests {

        @Test
        void getAllProducts_thenReturnsProducts() {

            saveANewRandomProduct(createSampleProduct());

            ResponseEntity<List<Product>> allProductsResponse = getAllProducts();

            log.info("All Products: {}", allProductsResponse.getBody());

            Assertions.assertThat(allProductsResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .isNotEmpty();
                    });
        }

        @Test
        void whenProductByIdExists_thenReturnsProduct() {
            Product newProduct = createSampleProduct();
            ResponseEntity<Product> createdProductResponse = saveANewRandomProduct(newProduct);
            Long productId = Objects.requireNonNull(createdProductResponse.getBody()).getId();

            ResponseEntity<Product> productResponse = getProductById(productId);
            Assertions.assertThat(productResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(product -> Assertions.assertThat(product.getId()).isEqualTo(productId));
                    });
        }

        @Test
        void whenProductByIdDoesNotExist_thenThrowsNotFound() {
            Assertions.assertThatThrownBy(() -> getProductById(Long.MAX_VALUE))
                    .isInstanceOf(HttpClientErrorException.NotFound.class);
        }

        @Test
        void whenGetProductsByPage_thenReturnsPageOfProducts() {

            List<@NonNull Product> savedProducts = new ArrayList<>();

            for (int i = 0; i < 100; i++) {
                ResponseEntity<Product> productResponseEntity = saveANewRandomProduct(createSampleProduct());
                if (productResponseEntity.getStatusCode().is2xxSuccessful()) {
                    savedProducts.add(Objects.requireNonNull(productResponseEntity.getBody()));
                }
            }

            String searchPrefix = savedProducts.getFirst().getName().substring(0, 1);

            ResponseEntity<PageResponse<Product>> pageResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/page")
                            .queryParam("name", searchPrefix)
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .queryParam("sortDirection", "asc")
                            .build())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            Assertions.assertThat(pageResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(products -> Assertions.assertThat(products)
                                        .extracting(PageResponse::content)
                                        .asInstanceOf(InstanceOfAssertFactories.LIST)
                                        .hasSizeLessThanOrEqualTo(10)
                                        .hasSizeGreaterThanOrEqualTo(1));
                    });

        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteProductTests {

        Long productId;

        @Test
        @Order(1)
        void deleteProductById_Successful() {

            ResponseEntity<Product> createdProductResponse = saveANewRandomProduct(createSampleProduct());
            Long id = Objects.requireNonNull(createdProductResponse.getBody()).getId();
            productId = String.valueOf(id).isBlank() ? id : Long.parseLong(String.valueOf(id));

            ResponseEntity<Void> productDeletionResponse = deleteProductById(productId);

            Assertions.assertThat(productDeletionResponse)
                    .isNotNull()
                    .satisfies(response -> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK));
        }

        @Test
        void deleteProductByNonExistenceId_thenIdempotent() {
            assertDoesNotThrow(() -> deleteProductById(productId))
                    .getStatusCode().is2xxSuccessful();
        }

        @Test
        void deleteProductById_whenProductDoesNotExist_thenReturnsSuccessful() {
            assertDoesNotThrow(() -> deleteProductById(Long.MAX_VALUE))
                    .getStatusCode().is2xxSuccessful();
        }
    }

    @Nested
    class PatchProductTests {

        private Product newProduct;

        @BeforeEach
        void beforeEach() {
            if (newProduct == null) {
                newProduct = Objects.requireNonNull(saveANewRandomProduct(createSampleProduct())
                        .getBody());
            }
        }

        @Test
        void whenValidPatch_thenAccepted() {
            Product patchedProduct = new Product();
            patchedProduct.setName(newProduct.getName() + " Patched");

            ResponseEntity<Void> productPatchResponse = restClient.patch()
                    .uri("/{id}", newProduct.getId())
                    .body(patchedProduct)
                    .retrieve()
                    .toBodilessEntity();

            Assertions.assertThat(productPatchResponse)
                    .isNotNull()
                    .satisfies(response ->
                            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));
        }

        @Test
        void testIdempotentPatch_thenSuccessful() {
            Product patchedProduct = new Product();
            patchedProduct.setName(newProduct.getName() + " Patched");

            assertDoesNotThrow(() -> restClient.patch()
                    .uri("/{id}", newProduct.getId())
                    .body(patchedProduct)
                    .retrieve()
                    .toBodilessEntity())
                    .getStatusCode().is2xxSuccessful();
        }
    }

    @Nested
    class PutProductTests {

        @Test
        void whenValidUpdate_thenAccepted() {
            Product newProduct = createSampleProduct();
            ResponseEntity<Product> createdProductResponse = saveANewRandomProduct(newProduct);

            Long productId = Objects.requireNonNull(createdProductResponse.getBody()).getId();
            newProduct.setName(newProduct.getName() + " Put");

            ResponseEntity<Void> putProductResponse = restClient.put()
                    .uri("/{id}", productId)
                    .body(newProduct)
                    .retrieve()
                    .toBodilessEntity();

            Assertions.assertThat(putProductResponse)
                    .isNotNull()
                    .satisfies(response ->
                            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED)
                    );
        }

        @Test
        void whenDuplicateProductNameUpdate_thenReturnsConflict() {
            Product newProduct = createSampleProduct();
            ResponseEntity<Product> createdProductResponse = saveANewRandomProduct(newProduct);

            Long productId = Objects.requireNonNull(createdProductResponse.getBody()).getId();
            newProduct.setName(newProduct.getName() + " Put");

            ResponseEntity<Void> productUpdateResponse = assertDoesNotThrow(() -> restClient.put()
                    .uri("/{id}", productId)
                    .body(newProduct)
                    .retrieve()
                    .toBodilessEntity());

            productUpdateResponse
                    .getStatusCode().is2xxSuccessful();
        }
    }

    private @NonNull Product createSampleProduct() {
        if (CollectionUtils.isEmpty(alreadyCommittedProductNames)) {
            ResponseEntity<List<Product>> allProductsResponse = this.getAllProducts();
            if (allProductsResponse.getStatusCode().is2xxSuccessful()) {
                alreadyCommittedProductNames = Objects.requireNonNull(allProductsResponse.getBody())
                        .stream()
                        .map(Product::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
        }

        return testDataFactory.getStreamOfProducts()
                .filter(product -> !alreadyCommittedProductNames.contains(product.getName()))
                .limit(1)
                .peek(product -> alreadyCommittedProductNames.add(product.getName()))
                .findFirst()
                .orElseThrow();
    }

    private @NonNull ResponseEntity<Product> saveANewRandomProduct(Product newProduct) {
        return restClient.post()
                .uri("/")
                .body(newProduct)
                .retrieve()
                .toEntity(Product.class);
    }

    private @NotNull ResponseEntity<List<Product>> getAllProducts() {
        return restClient.get()
                .uri("/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    private @NotNull ResponseEntity<Product> getProductById(Long productId) {
        return restClient.get()
                .uri("/{id}", productId)
                .retrieve()
                .toEntity(Product.class);
    }

    private @NotNull ResponseEntity<Void> deleteProductById(Long productId) {
        return restClient.delete()
                .uri("/{id}", productId)
                .retrieve()
                .toBodilessEntity();
    }
}
