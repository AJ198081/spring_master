package dev.aj.full_stack_v6.product.controller;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.common.domain.dtos.PageResponse;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.product.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-test.properties"})
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
    private UserAuthFactory userAuthFactory;

    @Autowired
    private ProductRepository productRepository;

    private RestClient restClient;

    private Set<String> alreadyCommittedProductNames = new HashSet<>();

    private HttpHeaders authTokenHeader;

    @BeforeAll
    void init() {
        userAuthFactory.setClients(port);
    }

    @BeforeEach
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d%s".formatted(port, PRODUCT_CONTROLLER_BASE_PATH));
        productRepository.deleteAll();

        if (authTokenHeader == null) {
            authTokenHeader = userAuthFactory.getBearerTokenHeader();
        }
    }

    @AfterEach
    void tearDown() {
        if (restClient != null) {
            restClient = null;
        }
    }

    @AfterAll
    void destroy() {
        userAuthFactory.resetClients();
    }

    @Nested
    class PostProductTests {

        @Test
        void whenValidProduct_thenCreatesProduct() {
            Product newProduct = createSampleProduct();
            ResponseEntity<Product> createdProductResponse = saveANewRandomProduct(newProduct);

            assertThat(createdProductResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(product -> assertThat(product.getName())
                                        .isNotNull()
                                        .isEqualTo(newProduct.getName()))
                                .extracting(Product::getAuditMetaData)
                                .satisfies(auditMetaData ->
                                        assertThat(auditMetaData)
                                                .isNotNull()
                                                .satisfies(metaData -> {
                                                    assertThat(metaData.getCreatedBy()).isNotBlank();
                                                    assertThat(metaData.getCreatedDate()).isInThePast();
                                                })
                                );

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

            assertThat(allProductsResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
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
            assertThat(productResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(product -> assertThat(product.getId()).isEqualTo(productId));
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
                    .headers(addBearerTokenHeaders())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            assertThat(pageResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(products -> assertThat(products)
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
            productId = Objects.requireNonNull(createdProductResponse.getBody()).getId();

            ResponseEntity<Void> productDeletionResponse = deleteProductById(productId);

            assertThat(productDeletionResponse)
                    .isNotNull()
                    .satisfies(response ->
                            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK)
                    );
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
                    .headers(addBearerTokenHeaders())
                    .body(patchedProduct)
                    .retrieve()
                    .toBodilessEntity();

            assertThat(productPatchResponse)
                    .isNotNull()
                    .satisfies(response ->
                            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));
        }

        @Test
        void testIdempotentPatch_thenSuccessful() {
            Product patchedProduct = new Product();
            patchedProduct.setName(newProduct.getName() + " Patched");

            assertDoesNotThrow(() -> restClient.patch()
                    .uri("/{id}", newProduct.getId())
                    .headers(addBearerTokenHeaders())
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
                    .headers(addBearerTokenHeaders())
                    .body(newProduct)
                    .retrieve()
                    .toBodilessEntity();

            assertThat(putProductResponse)
                    .isNotNull()
                    .satisfies(response ->
                            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED)
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
                    .headers(addBearerTokenHeaders())
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
                .headers(addBearerTokenHeaders())
                .body(newProduct)
                .retrieve()
                .toEntity(Product.class);
    }

    private @NotNull ResponseEntity<List<Product>> getAllProducts() {
        return restClient.get()
                .uri("/all")
                .headers(addBearerTokenHeaders())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    private @NotNull Consumer<HttpHeaders> addBearerTokenHeaders() {
        return headers -> headers.addAll(authTokenHeader);
    }

    private @NotNull ResponseEntity<Product> getProductById(Long productId) {
        return restClient.get()
                .uri("/{id}", productId)
                .headers(addBearerTokenHeaders())
                .retrieve()
                .toEntity(Product.class);
    }

    private @NotNull ResponseEntity<Void> deleteProductById(Long productId) {
        return restClient.delete()
                .uri("/{id}", productId)
                .headers(addBearerTokenHeaders())
                .retrieve()
                .toBodilessEntity();
    }
}
