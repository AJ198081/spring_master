package dev.aj.full_stack_v6.image.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.common.domain.entities.Image;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestConfig.class, TestDataFactory.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@AutoConfigureMockMvc(addFilters = false) // Disables Spring Security filters
class ImageControllerTest {

    private static final String IMAGE_BASE = "/api/v1/images";
    private static final String PRODUCT_BASE = "/api/v1/products";

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                        Assertions.assertThat(img.getDownloadUrl()).contains("/api/v1/images/".concat(img.getId().toString()));
                    });
        }

        @Test
        void whenAddImagesToProduct_thenReturnsImages() throws Exception {
            Product newProduct = testDataFactory.getStreamOfProducts()
                    .findFirst()
                    .orElseThrow();

            String productReq = objectMapper.writeValueAsString(newProduct);
            String productResp = mockMvc.perform(
                            MockMvcRequestBuilders.post(PRODUCT_BASE + "/")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(productReq)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            Long productId = Objects.requireNonNull(objectMapper.readValue(productResp, Product.class)).getId();

            MockMultipartFile f1 = testDataFactory.getRandomImageFile();
            MockMultipartFile f2 = testDataFactory.getRandomImageFile();
            MockMultipartFile f3 = testDataFactory.getRandomImageFile();

            mockMvc.perform(
                            MockMvcRequestBuilders.multipart(IMAGE_BASE + "/product/{productId}", productId)
                                    .file(f1)
                                    .file(f2)
                                    .file(f3)
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .param("replaceAll", "true")
                    )
                    .andExpect(status().isOk());

            String listJson = mockMvc.perform(
                            MockMvcRequestBuilders.get(IMAGE_BASE + "/product/{productId}", productId)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            List<Image> imagesByProduct = objectMapper.readValue(listJson, new TypeReference<List<Image>>() {});

            Assertions.assertThat(imagesByProduct)
                    .isNotNull()
                    .hasSizeGreaterThanOrEqualTo(3);

            String prodJson = mockMvc.perform(
                            MockMvcRequestBuilders.get(PRODUCT_BASE + "/{id}", productId)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            Product fetched = objectMapper.readValue(prodJson, Product.class);
            Assertions.assertThat(fetched)
                    .isNotNull();
            Assertions.assertThat(fetched.getImages()).hasSizeGreaterThanOrEqualTo(3);
            Assertions.assertThat(fetched.getImages())
                    .allMatch(image -> image.getDownloadUrl().contains("/api/v1/images/".concat(image.getId().toString())));
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

            String respJson = mockMvc.perform(
                            MockMvcRequestBuilders.get(IMAGE_BASE + "/all")
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            List<Image> allImages = objectMapper.readValue(respJson, new TypeReference<List<Image>>() {});
            Assertions.assertThat(allImages)
                    .isNotNull()
                    .hasSizeGreaterThanOrEqualTo(uploaded.size());
        }

        @Test
        void whenDownloadImageById_thenOk() throws Exception {
            Image saved = uploadOne(testDataFactory.getRandomImageFile());

            mockMvc.perform(
                            MockMvcRequestBuilders.get(IMAGE_BASE + "/download/{id}", saved.getId())
                    )
                    .andExpect(status().isOk());
        }

        @Test
        void whenGetImagesByProductId_thenReturnsImages() throws Exception {
            Product newProduct = testDataFactory.getStreamOfProducts().findFirst().orElseThrow();
            String productReq = objectMapper.writeValueAsString(newProduct);
            String productResp = mockMvc.perform(
                            MockMvcRequestBuilders.post(PRODUCT_BASE + "/")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(productReq)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);
            Long productId = Objects.requireNonNull(objectMapper.readValue(productResp, Product.class)).getId();

            mockMvc.perform(MockMvcRequestBuilders.multipart(IMAGE_BASE + "/product/{productId}", productId)
                            .file(testDataFactory.getRandomImageFile())
                            .file(testDataFactory.getRandomImageFile()))
                    .andExpect(status().isOk());

            String listJson2 = mockMvc.perform(
                            MockMvcRequestBuilders.get(IMAGE_BASE + "/product/{productId}", productId)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            List<Image> images2 = objectMapper.readValue(listJson2, new TypeReference<List<Image>>() {});
            Assertions.assertThat(images2)
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

            mockMvc.perform(
                            MockMvcRequestBuilders.delete(IMAGE_BASE + "/{id}", saved.getId())
                    )
                    .andExpect(status().is2xxSuccessful());

            assertDoesNotThrow(() -> {
                try {
                    mockMvc.perform(
                                    MockMvcRequestBuilders.delete(IMAGE_BASE + "/{id}", saved.getId())
                            )
                            .andExpect(status().is2xxSuccessful());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
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
