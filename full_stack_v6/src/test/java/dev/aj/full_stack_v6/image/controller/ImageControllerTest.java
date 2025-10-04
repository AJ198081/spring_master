package dev.aj.full_stack_v6.image.controller;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.common.domain.entities.Image;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class, UserAuthFactory.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ImageControllerTest {

    public static final String BASE_URL_FORMAT = "http://localhost:%d%s";
    @LocalServerPort
    private Integer port;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserAuthFactory userAuthFactory;

    @Autowired
    private Environment environment;

    private RestClient imageClient;
    private RestClient productClient;

    private HttpHeaders authTokenHeader;

    @BeforeAll
    void init() {
        userAuthFactory.setClients(port);
        if (authTokenHeader == null) {
            authTokenHeader = userAuthFactory.getBearerTokenHeader();
        }
        imageClient = userAuthFactory.authenticatedRestClient(BASE_URL_FORMAT.formatted(port, environment.getProperty("IMAGE_API_PATH")));
        productClient = userAuthFactory.authenticatedRestClient(BASE_URL_FORMAT.formatted(port, environment.getProperty("PRODUCT_API_PATH")));

        testDataFactory.saveSellerProfile(userAuthFactory.authenticatedRestClient(BASE_URL_FORMAT.formatted(port, environment.getProperty("SELLER_API_PATH"))));
    }

    @AfterAll
    void destroy() {
        imageClient = null;
        productClient = null;
        userAuthFactory.resetClients();
    }

    @Nested
    class PostImageTests {
        @Test
        void whenAddImage_thenReturnsSavedImage() {
            MockMultipartFile randomImageFile = testDataFactory.getRandomImageFile();

            Image image = uploadOne(randomImageFile);

            Assertions.assertThat(image)
                    .isNotNull()
                    .satisfies(img -> {
                        Assertions.assertThat(img.getId()).isNotNull();
                        Assertions.assertThat(img.getFileName()).isEqualTo(randomImageFile.getOriginalFilename());
                        Assertions.assertThat(img.getContentType()).isEqualTo(randomImageFile.getContentType());
                        Assertions.assertThat(img.getDownloadUrl()).contains(environment.getRequiredProperty("IMAGE_API_PATH").concat("/").concat(img.getId().toString()));
                    });
        }

        @Test
        void whenAddImagesToProduct_thenReturnsImages() {
            Product newProduct = testDataFactory.getStreamOfProducts()
                    .findFirst()
                    .orElseThrow();

            ResponseEntity<Product> productResponse = productClient.post()
                    .uri("/")
                    .body(newProduct)
                    .retrieve()
                    .toEntity(Product.class);

            Long productId = Objects.requireNonNull(productResponse.getBody()).getId();

            MockMultipartFile f1 = testDataFactory.getRandomImageFile();
            MockMultipartFile f2 = testDataFactory.getRandomImageFile();
            MockMultipartFile f3 = testDataFactory.getRandomImageFile();

            ResponseEntity<Void> uploadResponse = imageClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/product/{productId}").queryParam("replaceAll", true).build(productId))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(buildMultipart(f1, f2, f3))
                    .retrieve()
                    .toBodilessEntity();
            assertThat(uploadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            ResponseEntity<List<Image>> listResponse = imageClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            List<Image> imagesByProduct = listResponse.getBody();

            Assertions.assertThat(imagesByProduct)
                    .isNotNull()
                    .hasSizeGreaterThanOrEqualTo(3);

            ResponseEntity<Product> fetchedProductResp = productClient.get()
                    .uri("/{id}", productId)
                    .retrieve()
                    .toEntity(Product.class);

            Product fetched = fetchedProductResp.getBody();
            Assertions.assertThat(fetched)
                    .isNotNull();
            Assertions.assertThat(Objects.requireNonNull(fetched).getImages()).hasSizeGreaterThanOrEqualTo(3);
            Assertions.assertThat(fetched.getImages())
                    .allMatch(image -> image.getDownloadUrl().contains(environment.getRequiredProperty("IMAGE_API_PATH").concat("/").concat(image.getId().toString())));
        }
    }

    @Nested
    class GetImageTests {
        @Test
        void whenGetAllImages_thenReturnsList() {

            List<Image> uploaded = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                uploaded.add(uploadOne(testDataFactory.getRandomImageFile()));
            }

            ResponseEntity<List<Image>> resp = imageClient.get()
                    .uri("/all")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            List<Image> allImages = resp.getBody();

            Assertions.assertThat(allImages)
                    .isNotNull()
                    .hasSizeGreaterThanOrEqualTo(uploaded.size());
        }

        @Test
        void whenDownloadImageById_thenOk() {
            Image saved = uploadOne(testDataFactory.getRandomImageFile());

            ResponseEntity<Void> downloadResp = imageClient.get()
                    .uri("/download/{id}", saved.getId())
                    .retrieve()
                    .toBodilessEntity();

            assertThat(downloadResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void whenGetImagesByProductId_thenReturnsImages() {
            Product newProduct = testDataFactory.getStreamOfProducts().findFirst().orElseThrow();

            Long productId = Objects.requireNonNull(productClient.post()
                            .uri("/")
                            .body(newProduct)
                            .retrieve()
                            .toEntity(Product.class)
                            .getBody())
                    .getId();

            ResponseEntity<Void> uploadResp = imageClient.post()
                    .uri("/product/{productId}", productId)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(buildMultipart(testDataFactory.getRandomImageFile(), testDataFactory.getRandomImageFile()))
                    .retrieve()
                    .toBodilessEntity();
            assertThat(uploadResp.getStatusCode()).isEqualTo(HttpStatus.OK);

            ResponseEntity<List<Image>> listResp = imageClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            List<Image> images2 = listResp.getBody();
            Assertions.assertThat(images2)
                    .isNotNull()
                    .hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested
    class PutImageTests {
        @Test
        void whenUpdateImage_thenOk() {
            Image saved = uploadOne(testDataFactory.getRandomImageFile());

            MockMultipartFile newFile = testDataFactory.getRandomImageFile();

            ResponseEntity<Image> updateResp = imageClient.put()
                    .uri("/{id}", saved.getId())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(buildMultipart(newFile))
                    .retrieve()
                    .toEntity(Image.class);

            Image updated = updateResp.getBody();
            Assertions.assertThat(Objects.requireNonNull(updated).getFileName()).isEqualTo(newFile.getOriginalFilename());
        }
    }

    @Nested
    class DeleteImageTests {
        @Test
        void whenDeleteImage_thenIdempotent() {
            Image saved = uploadOne(testDataFactory.getRandomImageFile());

            ResponseEntity<Void> deleteResp = imageClient.delete()
                    .uri("/{id}", saved.getId())
                    .retrieve()
                    .toBodilessEntity();
            assertThat(deleteResp.getStatusCode().is2xxSuccessful()).isTrue();

            assertDoesNotThrow(() -> imageClient.delete()
                    .uri("/{id}", saved.getId())
                    .retrieve()
                    .toBodilessEntity())
                    .getStatusCode().is2xxSuccessful();
        }
    }

    @SneakyThrows
    private Image uploadOne(MockMultipartFile file) {
        ResponseEntity<Image> resp = imageClient.post()
                .uri("/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(buildMultipart(file))
                .retrieve()
                .toEntity(Image.class);
        return resp.getBody();
    }

    private MultiValueMap<String, HttpEntity<?>> buildMultipart(MockMultipartFile... files) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        for (MockMultipartFile file : files) {
            builder.part("files", file.getResource())
                    .filename(file.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(Objects.requireNonNullElse(file.getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE)));
        }
        return builder.build();
    }
}
