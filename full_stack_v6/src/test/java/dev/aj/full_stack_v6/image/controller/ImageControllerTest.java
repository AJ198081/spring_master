package dev.aj.full_stack_v6.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.common.domain.entities.Image;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class})
@TestPropertySource(locations = {"classpath:application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@AutoConfigureMockMvc
class ImageControllerTest {

    private static final String IMAGE_BASE = "/api/v1/images";
    private static final String PRODUCT_BASE = "/api/v1/products";

    @LocalServerPort
    private int port;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RestClient imageClient;
    private RestClient productClient;

    @BeforeEach
    void setUp() {
        this.imageClient = testConfig.restClient("http://localhost:%d%s".formatted(port, IMAGE_BASE));
        this.productClient = testConfig.restClient("http://localhost:%d%s".formatted(port, PRODUCT_BASE));
    }

    @Nested
    class PostImageTests {
        @Test
        void whenAddImage_thenReturnsSavedImage() throws Exception {
            MockMultipartFile randomImageFile = testDataFactory.getRandomImageFile();

            String responseJson = mockMvc.perform(multipart(IMAGE_BASE + "/")
                            .file(randomImageFile))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            Image image = objectMapper.readValue(responseJson, Image.class);

            Assertions.assertThat(image)
                    .isNotNull()
                    .satisfies(img -> {
                        Assertions.assertThat(img.getId()).isNotNull();
                        Assertions.assertThat(img.getFileName()).isEqualTo(randomImageFile.getOriginalFilename());
                        Assertions.assertThat(img.getContentType()).isEqualTo(randomImageFile.getContentType());
                        Assertions.assertThat(img.getDownloadUrl()).contains("download/");
                    });
        }

        @Test
        void whenAddImagesToProduct_thenReturnsImages() throws Exception {
            Product newProduct = testDataFactory.getStreamOfProducts()
                    .findFirst()
                    .orElseThrow();

            Long productId = Objects.requireNonNull(productClient.post()
                            .uri("/")
                            .body(newProduct)
                            .retrieve()
                            .toEntity(Product.class)
                            .getBody())
                    .getId();

            MockMultipartFile f1 = testDataFactory.getRandomImageFile();
            MockMultipartFile f2 = testDataFactory.getRandomImageFile();
            MockMultipartFile f3 = testDataFactory.getRandomImageFile();

            mockMvc.perform(MockMvcRequestBuilders.multipart(IMAGE_BASE + "/product/{productId}", productId)
                            .file(f1)
                            .file(f2)
                            .file(f3)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("replaceAll", "true"))
                    .andExpect(status().isOk());

            ResponseEntity<List<Image>> listResp = imageClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            Assertions.assertThat(listResp.getBody())
                    .isNotNull()
                    .hasSizeGreaterThanOrEqualTo(3);

            ResponseEntity<Product> productResponseEntity = productClient.get()
                    .uri("/{id}", productId)
                    .retrieve()
                    .toEntity(Product.class);

            Assertions.assertThat(productResponseEntity)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        Assertions.assertThat(response.getBody()).isNotNull();
                        Assertions.assertThat(response.getBody().getImages()).hasSizeGreaterThanOrEqualTo(3);
                    })
                    .extracting(ResponseEntity::getBody)
                    .extracting(Product::getImages)
                    .satisfies(images ->
                            Assertions.assertThat(images)
                                    .allMatch(image -> !image.getDownloadUrl().isBlank()));
        }
    }

    @Nested
    class GetImageTests {
        @Test
        void whenGetAllImages_thenReturnsList() throws Exception {
            // add a few images first
            List<Image> uploaded = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                uploaded.add(uploadOne(testDataFactory.getRandomImageFile()));
            }

            ResponseEntity<List<Image>> response = imageClient.get()
                    .uri("/all")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            Assertions.assertThat(response.getBody())
                    .isNotNull()
                    .hasSizeGreaterThanOrEqualTo(uploaded.size());
        }

        @Test
        void whenDownloadImageById_thenOk() throws Exception {
            Image saved = uploadOne(testDataFactory.getRandomImageFile());

            ResponseEntity<Resource> resp = imageClient.get()
                    .uri("/download/{id}", saved.getId())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            Assertions.assertThat(resp)
                    .isNotNull()
                    .satisfies(r -> Assertions.assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK));
        }

        @Test
        void whenGetImagesByProductId_thenReturnsImages() throws Exception {
            Product newProduct = testDataFactory.getStreamOfProducts().findFirst().orElseThrow();
            Long productId = Objects.requireNonNull(productClient.post()
                    .uri("/")
                    .body(newProduct)
                    .retrieve()
                    .toEntity(Product.class)
                    .getBody()).getId();

            mockMvc.perform(MockMvcRequestBuilders.multipart(IMAGE_BASE + "/product/{productId}", productId)
                            .file(testDataFactory.getRandomImageFile())
                            .file(testDataFactory.getRandomImageFile()))
                    .andExpect(status().isOk());

            ResponseEntity<List<Image>> listResp = imageClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            Assertions.assertThat(listResp.getBody())
                    .isNotNull()
                    .hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested
    class PutImageTests {
        @Test
        void whenUpdateImage_thenOk() throws Exception {
            Image saved = uploadOne(testDataFactory.getRandomImageFile());

            MockMultipartFile newFile = testDataFactory.getRandomImageFile();

            String json = mockMvc.perform(MockMvcRequestBuilders.multipart(IMAGE_BASE + "/{id}", saved.getId())
                            .file(newFile)
                            .with(req -> {
                                req.setMethod("PUT");
                                return req;
                            }))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            Image updated = objectMapper.readValue(json, Image.class);
            Assertions.assertThat(updated.getFileName()).isEqualTo(newFile.getOriginalFilename());
        }
    }

    @Nested
    class DeleteImageTests {
        @Test
        void whenDeleteImage_thenIdempotent() throws Exception {
            Image saved = uploadOne(testDataFactory.getRandomImageFile());

            ResponseEntity<Void> resp = imageClient.delete()
                    .uri("/{id}", saved.getId())
                    .retrieve()
                    .toBodilessEntity();

            Assertions.assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();

            assertDoesNotThrow(() -> imageClient.delete()
                    .uri("/{id}", saved.getId())
                    .retrieve()
                    .toBodilessEntity());
        }
    }

    @SneakyThrows
    private Image uploadOne(MockMultipartFile file) {
        String responseJson = mockMvc.perform(
                        MockMvcRequestBuilders.multipart(IMAGE_BASE + "/").file(file)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(responseJson, Image.class);
    }
}
