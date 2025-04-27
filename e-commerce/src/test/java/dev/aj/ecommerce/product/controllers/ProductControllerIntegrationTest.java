package dev.aj.ecommerce.product.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.ecommerce.PostgresTCConfig;
import dev.aj.ecommerce.TestConfig;
import dev.aj.ecommerce.TestData;
import dev.aj.ecommerce.auth.domain.dtos.AuthResponseDto;
import dev.aj.ecommerce.auth.domain.dtos.UserLoginDto;
import dev.aj.ecommerce.auth.domain.dtos.UserRegistrationDto;
import dev.aj.ecommerce.auth.domain.entities.User;
import dev.aj.ecommerce.auth.domain.entities.enums.Role;
import dev.aj.ecommerce.auth.repositories.AuthRepository;
import dev.aj.ecommerce.product.domain.dtos.ProductRequest;
import dev.aj.ecommerce.product.domain.dtos.ProductResponse;
import dev.aj.ecommerce.product.domain.entities.Product;
import dev.aj.ecommerce.product.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Import(value = {PostgresTCConfig.class, TestConfig.class, TestData.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestConfig testConfig;

    @LocalServerPort
    private int port;

    private RestClient restClient;
    private String accessToken;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";

    private static final String TEST_PRODUCT_NAME = "Test Product";
    private static final String TEST_PRODUCT_DESCRIPTION = "Test Description";
    private static final BigDecimal TEST_PRODUCT_PRICE = new BigDecimal("99.99");
    private static final String TEST_PRODUCT_IMAGE_URL = "https://example.com/image.jpg";

    @BeforeAll
    void beforeAll() {
        restClient = testConfig.restClient("http://localhost:" + port);
    }

    @BeforeEach
    void setUp() {
        // Clear any existing test products and users
        productRepository.deleteAll();
        authRepository.findByUsername(TEST_USERNAME).ifPresent(authRepository::delete);
        authRepository.findByEmail(TEST_EMAIL).ifPresent(authRepository::delete);

        // Create a test user and authenticate
        createTestUserAndAuthenticate();
    }

    private void createTestUserAndAuthenticate() {
        // Create a test user
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setRole(Role.ROLE_USER);
        authRepository.save(user);

        // Authenticate and get access token
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail(TEST_USERNAME);
        loginDto.setPassword(TEST_PASSWORD);

        ResponseEntity<AuthResponseDto> loginResponse = restClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginDto)
                .retrieve()
                .toEntity(AuthResponseDto.class);

        AuthResponseDto response = loginResponse.getBody();
        assertThat(response).isNotNull();
        accessToken = response.getAccessToken();
        assertThat(accessToken).isNotNull();
    }

    @Test
    void testCreateProduct() {
        // Create a product request
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName(TEST_PRODUCT_NAME);
        productRequest.setDescription(TEST_PRODUCT_DESCRIPTION);
        productRequest.setPrice(TEST_PRODUCT_PRICE);
        productRequest.setImageUrl(TEST_PRODUCT_IMAGE_URL);
        productRequest.setFeatured(true);

        // Send the request to create a product
        ResponseEntity<ProductResponse> createResponse = restClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(productRequest)
                .retrieve()
                .toEntity(ProductResponse.class);

        // Verify the response
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ProductResponse createdProduct = createResponse.getBody();
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo(TEST_PRODUCT_NAME);
        assertThat(createdProduct.getDescription()).isEqualTo(TEST_PRODUCT_DESCRIPTION);
        assertThat(createdProduct.getPrice()).isEqualByComparingTo(TEST_PRODUCT_PRICE);
        assertThat(createdProduct.getImageUrl()).isEqualTo(TEST_PRODUCT_IMAGE_URL);
        assertThat(createdProduct.isFeatured()).isTrue();

        // Verify the product was saved to the database
        Product savedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo(TEST_PRODUCT_NAME);
        assertThat(savedProduct.getDescription()).isEqualTo(TEST_PRODUCT_DESCRIPTION);
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(TEST_PRODUCT_PRICE);
        assertThat(savedProduct.getImageUrl()).isEqualTo(TEST_PRODUCT_IMAGE_URL);
        assertThat(savedProduct.isFeatured()).isTrue();
    }

    @Test
    void testGetProductById() {
        // Create a test product in the database
        Product product = Product.builder()
                .name(TEST_PRODUCT_NAME)
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(true)
                .build();
        Product savedProduct = productRepository.save(product);

        // Get the product by ID
        ResponseEntity<ProductResponse> getResponse = restClient.get()
                .uri("/api/products/" + savedProduct.getId())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(ProductResponse.class);

        // Verify the response
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductResponse retrievedProduct = getResponse.getBody();
        assertThat(retrievedProduct).isNotNull();
        assertThat(retrievedProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(retrievedProduct.getName()).isEqualTo(TEST_PRODUCT_NAME);
        assertThat(retrievedProduct.getDescription()).isEqualTo(TEST_PRODUCT_DESCRIPTION);
        assertThat(retrievedProduct.getPrice()).isEqualByComparingTo(TEST_PRODUCT_PRICE);
        assertThat(retrievedProduct.getImageUrl()).isEqualTo(TEST_PRODUCT_IMAGE_URL);
        assertThat(retrievedProduct.isFeatured()).isTrue();
    }

    @Test
    void testGetAllProducts() {
        // Create multiple test products in the database
        Product product1 = Product.builder()
                .name(TEST_PRODUCT_NAME + " 1")
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(true)
                .build();
        Product product2 = Product.builder()
                .name(TEST_PRODUCT_NAME + " 2")
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(false)
                .build();
        productRepository.saveAll(List.of(product1, product2));

        // Get all products
        ResponseEntity<ProductResponse[]> getResponse = restClient.get()
                .uri("/api/products")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(ProductResponse[].class);

        // Verify the response
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductResponse[] products = getResponse.getBody();
        assertThat(products).isNotNull();
        assertThat(products.length).isEqualTo(2);
    }

    @Test
    void testGetFeaturedProducts() {
        // Create multiple test products in the database, some featured and some not
        Product product1 = Product.builder()
                .name(TEST_PRODUCT_NAME + " 1")
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(true)
                .build();
        Product product2 = Product.builder()
                .name(TEST_PRODUCT_NAME + " 2")
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(false)
                .build();
        Product product3 = Product.builder()
                .name(TEST_PRODUCT_NAME + " 3")
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(true)
                .build();
        productRepository.saveAll(List.of(product1, product2, product3));

        // Get featured products
        ResponseEntity<ProductResponse[]> getResponse = restClient.get()
                .uri("/api/products/featured")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(ProductResponse[].class);

        // Verify the response
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductResponse[] products = getResponse.getBody();
        assertThat(products).isNotNull();
        assertThat(products.length).isEqualTo(2);
        assertThat(products[0].isFeatured()).isTrue();
        assertThat(products[1].isFeatured()).isTrue();
    }

    @Test
    void testSearchProducts() {
        // Create multiple test products in the database with different names
        Product product1 = Product.builder()
                .name("iPhone 13")
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(true)
                .build();
        Product product2 = Product.builder()
                .name("Samsung Galaxy")
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(false)
                .build();
        Product product3 = Product.builder()
                .name("iPhone 14")
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(true)
                .build();
        productRepository.saveAll(List.of(product1, product2, product3));

        // Search for products with "iPhone" in the name
        ResponseEntity<ProductResponse[]> searchResponse = restClient.get()
                .uri("/api/products/search?query=iPhone")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(ProductResponse[].class);

        // Verify the response
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductResponse[] products = searchResponse.getBody();
        assertThat(products).isNotNull();
        assertThat(products.length).isEqualTo(2);
        assertThat(products[0].getName()).contains("iPhone");
        assertThat(products[1].getName()).contains("iPhone");
    }

    @Test
    void testUpdateProduct() {
        // Create a test product in the database
        Product product = Product.builder()
                .name(TEST_PRODUCT_NAME)
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(false)
                .build();
        Product savedProduct = productRepository.save(product);

        // Create an update request
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Product Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(new BigDecimal("149.99"));
        updateRequest.setImageUrl("https://example.com/updated-image.jpg");
        updateRequest.setFeatured(true);

        // Send the update request
        ResponseEntity<ProductResponse> updateResponse = restClient.put()
                .uri("/api/products/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateRequest)
                .retrieve()
                .toEntity(ProductResponse.class);

        // Verify the response
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductResponse updatedProduct = updateResponse.getBody();
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product Name");
        assertThat(updatedProduct.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("149.99"));
        assertThat(updatedProduct.getImageUrl()).isEqualTo("https://example.com/updated-image.jpg");
        assertThat(updatedProduct.isFeatured()).isTrue();

        // Verify the product was updated in the database
        Product dbProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(dbProduct.getName()).isEqualTo("Updated Product Name");
        assertThat(dbProduct.getDescription()).isEqualTo("Updated Description");
        assertThat(dbProduct.getPrice()).isEqualByComparingTo(new BigDecimal("149.99"));
        assertThat(dbProduct.getImageUrl()).isEqualTo("https://example.com/updated-image.jpg");
        assertThat(dbProduct.isFeatured()).isTrue();
    }

    @Test
    void testDeleteProduct() {
        // Create a test product in the database
        Product product = Product.builder()
                .name(TEST_PRODUCT_NAME)
                .description(TEST_PRODUCT_DESCRIPTION)
                .price(TEST_PRODUCT_PRICE)
                .imageUrl(TEST_PRODUCT_IMAGE_URL)
                .isFeatured(false)
                .build();
        Product savedProduct = productRepository.save(product);

        // Delete the product
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/api/products/" + savedProduct.getId())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(Void.class);

        // Verify the response
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify the product was deleted from the database
        assertThat(productRepository.existsById(savedProduct.getId())).isFalse();
    }
}
