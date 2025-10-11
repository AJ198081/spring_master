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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"/application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
class CartControllerTest {

    public static final String BASE_URL_FORMAT = "http://localhost:%d%s";
    @LocalServerPort
    private Integer port;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserAuthFactory userAuthFactory;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Environment environment;

    private RestClient authenticatedCartClient;

    private RestClient authenticatedProductClient;

    private RestClient authenticatedSellerClient;

    private RestClient authenticatedCustomerClient;

    @BeforeAll
    void init() {
        userAuthFactory.setClients(port);
        instantiateAuthenticatedClients();
        saveCustomerAndSellerProfilesForLoggedInUser();
    }

    @AfterAll
    void destroy() {
        userAuthFactory.resetClients();
        resetClientsForThis();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(1)
    class PostCartTests {

        private Product newProduct;

        @BeforeAll
        void beforeEach() {
            if (newProduct == null && authenticatedProductClient != null) {
                newProduct = addANewProduct();
            }
        }

        @Test
        @Order(1)
        void whenAuthenticatedUser_AddsProductToCart_Successful() {

            Assertions.assertThat(newProduct)
                    .isNotNull()
                    .satisfies(p -> Assertions.assertThat(p.getStock()).isGreaterThan(1));

            int quantityToOrder = newProduct.getStock() / 2;

            ResponseEntity<Void> addToCartResponse = addProductToCart(newProduct.getId(), quantityToOrder);

            Assertions.assertThat(addToCartResponse)
                    .isNotNull()
                    .satisfies(response -> response.getStatusCode().is2xxSuccessful());

            newProduct.setStock(newProduct.getStock() - quantityToOrder);
        }

        @Test
        @Order(2)
        void whenAuthenticatedUser_AddsProductWithZeroOrNegativeQuantity_ThenThrowsBadRequest() {
            Assertions.assertThat(newProduct)
                    .isNotNull();

            Assertions.assertThatThrownBy(() -> addProductToCart(newProduct.getId(), 0))
                    .isInstanceOf(HttpClientErrorException.BadRequest.class);

            Assertions.assertThatThrownBy(() -> addProductToCart(newProduct.getId(), -1))
                    .isInstanceOf(HttpClientErrorException.BadRequest.class);
        }

        @Test
        @Order(3)
        void whenAuthenticatedUser_AddsProducts_SuccessfullyAddsToExistingCart() {

            Assertions.assertThat(newProduct)
                    .isNotNull()
                    .satisfies(p -> Assertions.assertThat(p.getStock()).isGreaterThan(0));

            ResponseEntity<Void> addToCartResponse = addProductToCart(newProduct.getId(), newProduct.getStock());

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
        @Order(4)
        void whenAuthenticatedUser_AddsProductWithInsufficientQuantity_ThenThrowsConflict() {
            Assertions.assertThat(newProduct);
            Assertions.assertThat(newProduct.getStock()).isEqualTo(0);

            Assertions.assertThatThrownBy(() -> addProductToCart(newProduct.getId(), 1))
                    .isInstanceOf(HttpClientErrorException.Conflict.class);
        }


    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PutCartTests {
        private Product newProduct;

        @BeforeAll
        void beforeEach() {
            if (newProduct == null && authenticatedProductClient != null) {
                newProduct = addANewProduct();
            }
        }

        @Test
        @Order(1)
        void whenAuthenticatedUser_PatchesCart_Successful() {
            Assertions.assertThat(newProduct)
                    .as("New Product stock level")
                    .isNotNull()
                    .satisfies(p -> Assertions.assertThat(p.getStock()).isGreaterThan(2));

            int quantity = newProduct.getStock() / 2;

            ResponseEntity<Cart> putProductResponse = putProductToCart(newProduct.getId(), quantity);

            Assertions.assertThat(putProductResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        response.getStatusCode().is2xxSuccessful();
                        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody());
                        Assertions.assertThat(response.getBody())
                                .isNotNull();
                        Assertions.assertThat(response.getBody().getCartItems())
                                .isNotEmpty()
                                .hasSizeGreaterThanOrEqualTo(1)
                                .filteredOn(cartItem -> cartItem.getProduct().getId().equals(newProduct.getId()))
                                .allSatisfy(cartItem -> {
                                    Assertions.assertThat(cartItem.getQuantity()).isEqualTo(quantity);
                                    Assertions.assertThat(cartItem.getProduct().getId()).isEqualTo(newProduct.getId());
                                });
                    });
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
            ResponseEntity<Cart> getCartResponse = getUserCart();

            Assertions.assertThat(getCartResponse)
                    .isNotNull()
                    .satisfies(response -> response.getStatusCode().is2xxSuccessful());
        }

        @Test
        @Order(2)
        void whenAuthenticatedAdminUser_GetsAllCarts_Successful() {

            String _ = userAuthFactory.loginAndReturnAdminJwt();
            instantiateAuthenticatedClients();
            saveCustomerAndSellerProfilesForLoggedInUser();
            addANewProduct();

            ResponseEntity<List<Cart>> getAllCartsResponse = authenticatedCartClient.get()
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
        void whenAuthenticatedNonAdminUser_GetsAllCarts_ThenThrowsForbidden() {

            String _ = userAuthFactory.loginAndReturnNonAdminJwt();
            instantiateAuthenticatedClients();
            saveCustomerAndSellerProfilesForLoggedInUser();
            addANewProduct();

            Assertions.assertThatThrownBy(() -> authenticatedCartClient.get()
                            .uri("/all")
                            .retrieve()
                            .toEntity(new ParameterizedTypeReference<>() {
                            }))
                    .isInstanceOf(HttpClientErrorException.Forbidden.class);
        }


    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(3)
    class DeleteCartTests {
        @Test
        @Order(1)
        void whenAuthenticatedUser_DeletesCart_Successful() {
            Product newlyAddedProduct = addANewProduct();

            addProductToCart(newlyAddedProduct.getId(), newlyAddedProduct.getStock());
            addProductToCart(addANewProduct().getId(), 1);

            ResponseEntity<Void> deleteCartResponse = authenticatedCartClient.delete()
                    .uri("/product", uriBuilder -> uriBuilder
                            .queryParam("productId", newlyAddedProduct.getId())
                            .build())
                    .retrieve()
                    .toBodilessEntity();

            Assertions.assertThat(deleteCartResponse)
                    .isNotNull()
                    .satisfies(response -> response.getStatusCode().is2xxSuccessful());
        }


    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(5)
    class UpdateCartInResponseToProductPriceUpdate {
        Product newProduct;

        @BeforeAll
        void beforeEach() {
            newProduct = addANewProduct();
        }

        @Test
        @Order(1)
        void whenAuthenticatedUser_DeletesAllCarts_Successful() {

            addProductToCart(newProduct.getId(), 1);

            BigDecimal updatedPrice = newProduct.getPrice().subtract(BigDecimal.ONE);
            newProduct.setPrice(updatedPrice);
            ResponseEntity<Void> productResponseEntity = testDataFactory.putAnExistingProduct(newProduct.getId(), newProduct, authenticatedProductClient);

            Assertions.assertThat(productResponseEntity)
                    .isNotNull()
                    .satisfies(productResponse -> productResponse.getStatusCode().is2xxSuccessful());

            ResponseEntity<Cart> userCartResponse = getUserCart();

            Assertions.assertThat(userCartResponse)
                    .isNotNull()
                    .satisfies(cartResponse -> {
                        Assertions.assertThat(cartResponse.getStatusCode().is2xxSuccessful());
                        Assertions.assertThat(cartResponse.getBody())
                                .isNotNull()
                                .extracting(Cart::getCartItems)
                                .isInstanceOf(List.class)
                                .satisfies(cartItems -> Assertions.assertThat(cartItems)
                                        .filteredOn(item -> item.getProduct().getId().equals(newProduct.getId()))
                                        .hasSize(1)
                                        .first()
                                        .satisfies(item -> Assertions.assertThat(item.getProduct().getPrice()).isEqualTo(updatedPrice)));
                    });

        }


    }
    protected Product addANewProduct() {
        AtomicReference<Product> created = new AtomicReference<>();
        testDataFactory.getStreamOfProducts()
                .findFirst()
                .ifPresent(product -> created.set(testDataFactory
                        .saveANewProduct(product, authenticatedProductClient)
                        .getBody()));
        return created.get();
    }
    protected void instantiateAuthenticatedClients() {
        resetClientsForThis();

        authenticatedCartClient = userAuthFactory.authenticatedRestClient(
                BASE_URL_FORMAT.formatted(
                        port,
                        environment.getProperty("CART_API_PATH"))
        );

        authenticatedProductClient = userAuthFactory.authenticatedRestClient(
                BASE_URL_FORMAT.formatted(
                        port,
                        environment.getProperty("PRODUCT_API_PATH"))
        );

        authenticatedCustomerClient = userAuthFactory.authenticatedRestClient(
                BASE_URL_FORMAT.formatted(
                        port,
                        environment.getProperty("CUSTOMER_API_PATH"))
        );

        authenticatedSellerClient = userAuthFactory.authenticatedRestClient(
                BASE_URL_FORMAT.formatted(
                        port,
                        environment.getProperty("SELLER_API_PATH"))
        );
    }

    protected @NotNull ResponseEntity<Void> addProductToCart(Long productId, int quantityToOrder) {
        return authenticatedCartClient.post()
                .uri("/", uriBuilder -> {
                            uriBuilder.queryParam("productId", productId)
                                    .queryParam("quantity", quantityToOrder);
                            return uriBuilder.build();
                        }
                )
                .retrieve()
                .toBodilessEntity();
    }

    protected @NotNull ResponseEntity<Cart> putProductToCart(Long productId, int updatedQuantity) {
        return authenticatedCartClient.put()
                .uri("/", uriBuilder -> {
                    uriBuilder.queryParam("productId", productId)
                            .queryParam("quantity", updatedQuantity);
                    return uriBuilder.build();
                })
                .retrieve()
                .toEntity(Cart.class);
    }

    protected @NotNull ResponseEntity<Cart> getUserCart() {
        return authenticatedCartClient.get()
                .uri("/")
                .retrieve()
                .toEntity(Cart.class);
    }

    private void resetClientsForThis() {
        authenticatedCartClient = null;
        authenticatedProductClient = null;
        authenticatedSellerClient = null;
        authenticatedCustomerClient = null;
    }

    private void saveCustomerAndSellerProfilesForLoggedInUser() {
        testDataFactory.saveSellerProfile(authenticatedSellerClient);
        testDataFactory.saveCustomerProfile(authenticatedCustomerClient);
    }
}