package dev.aj.full_stack_v6.cart.controllers;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.common.domain.entities.Cart;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.product.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
class CartControllerTest {


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

    @Autowired
    private Environment environment;

    private RestClient cartClient;

    private RestClient productAuthenticatedClient;

    private HttpHeaders authTokenHeader;

    @BeforeAll
    void init() {
        userAuthFactory.setClients(port);
        instantiateClients();
    }

    @AfterAll
    void destroy() {
        userAuthFactory.resetClients();
        cartClient = null;
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(1)
    class PostCartTests {

        private Product newProduct;

        @BeforeAll
        void beforeEach() {
            if (newProduct == null && productAuthenticatedClient != null) {
                newProduct = addANewProduct();
            }
        }

        @Test
        @Order(1)
        void whenAuthenticatedUser_AddsProductToCart_Successful() {

            Assertions.assertThat(newProduct);
            Assertions.assertThat(newProduct.getStock()).isGreaterThan(0);
            int quantityToOrder = newProduct.getStock() / 2;

            ResponseEntity<Void> addToCartResponse = addProductToCart(newProduct, quantityToOrder);

            Assertions.assertThat(addToCartResponse)
                    .isNotNull()
                    .satisfies(response -> response.getStatusCode().is2xxSuccessful());

            newProduct.setStock(newProduct.getStock() - quantityToOrder);
        }

        @Test
        @Order(2)
        void whenAuthenticatedUser_AddsProducts_SuccessfullyAddsToExistingCart() {

            Assertions.assertThat(newProduct);

            ResponseEntity<Void> addToCartResponse = addProductToCart(newProduct, newProduct.getStock());

            Assertions.assertThat(addToCartResponse)
                    .isNotNull()
                    .satisfies(response -> response.getStatusCode().is2xxSuccessful());

            newProduct.setStock(0);

            Product product = productRepository.findById(newProduct.getId())
                    .orElseThrow();

            Assertions.assertThat(product)
                    .isNotNull()
                    .satisfies(p -> Assertions.assertThat(p.getStock()).isEqualTo(0));
        }

        @Test
        @Order(3)
        void whenAuthenticatedUser_AddsProductWithInsufficientQuantity_ThenThrowsConflict() {
            Assertions.assertThat(newProduct);
            Assertions.assertThat(newProduct.getStock()).isEqualTo(0);

            Assertions.assertThatThrownBy(() -> addProductToCart(newProduct, 1))
                    .isInstanceOf(HttpClientErrorException.Conflict.class);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(2)
    class GetCartTests {

        @Test
        @Order(1)
        void whenAuthenticatedUser_GetsCart_Successful() {
            ResponseEntity<Void> getCartResponse = cartClient.get()
                    .uri("/")
                    .retrieve()
                    .toBodilessEntity();

            Assertions.assertThat(getCartResponse)
                    .isNotNull()
                    .satisfies(response -> response.getStatusCode().is2xxSuccessful());
        }

        @Test
        @Order(2)
        void whenAuthenticatedAdminUser_GetsAllCarts_Successful() {

            String _ = userAuthFactory.loginAndReturnAdminJwt();
            instantiateClients();
            addANewProduct();


            ResponseEntity<List<Cart>> getAllCartsResponse = cartClient.get()
                    .uri("/all")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            Assertions.assertThat(getAllCartsResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        response.getStatusCode().is2xxSuccessful();
                        Assertions.assertThat(response.getBody())
                                .isNotEmpty()
                                .hasSizeGreaterThanOrEqualTo(1);
                    });
        }

        @Test
        @Order(3)
        void whenAuthenticatedNonAdminUser_GetsAllCarts_ThenThrows() {

            String _ = userAuthFactory.loginAndReturnNonAdminJwt();
            instantiateClients();
            addANewProduct();

            Assertions.assertThatThrownBy(() -> cartClient.get()
                            .uri("/all")
                            .retrieve()
                            .toEntity(new ParameterizedTypeReference<>() {
                            }))
                    .isInstanceOf(HttpClientErrorException.Forbidden.class);
        }
    }

    protected Product addANewProduct() {
        AtomicReference<Product> created = new AtomicReference<>();
        testDataFactory.getStreamOfProducts()
                .findFirst()
                .ifPresent(product -> created.set(testDataFactory
                        .saveANewRandomProduct(product, productAuthenticatedClient)
                        .getBody()));
        return created.get();
    }

    protected void instantiateClients() {
        cartClient = userAuthFactory.authenticatedRestClient(
                "http://localhost:%d%s".formatted(
                        port,
                        environment.getProperty("CART_API_PATH"))
        );

        productAuthenticatedClient = userAuthFactory.authenticatedRestClient(
                "http://localhost:%d%s".formatted(
                        port,
                        environment.getProperty("PRODUCT_API_PATH"))
        );
    }

    protected @NotNull ResponseEntity<Void> addProductToCart(Product product, int quantityToOrder) {
        return cartClient.post()
                .uri("/", uriBuilder -> {
                            uriBuilder.queryParam("productId", product.getId())
                                    .queryParam("quantity", quantityToOrder);
                            return uriBuilder.build();
                        }
                )
                .retrieve()
                .toBodilessEntity();
    }
}