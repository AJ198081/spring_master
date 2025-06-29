package dev.aj.full_stack_v5.product.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v5.InitSecurityUser;
import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.product.domain.dtos.ImageResponseDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductResponseDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class, InitSecurityUser.class})
@TestPropertySource(locations = {"classpath:application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@AutoConfigureMockMvc
class ImageControllerTest {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private PhotosFactory photosFactory;

    @Autowired
    private InitSecurityUser initSecurityUser;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders bearerTokenHeader;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));
        bearerTokenHeader = initSecurityUser.getBearerTokenHeader(restClient);

        // Create a product first to associate images with
        ResponseEntity<ProductResponseDto> productResponse = restClient.post()
                .uri("/api/v1/products/")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .body(testDataFactory.generateStreamOfProductRequests().findFirst().orElseThrow())
                .retrieve()
                .toEntity(ProductResponseDto.class);

        assertNotNull(productResponse.getBody());
        Long productId = productResponse.getBody().getId();
        log.info("Created product with ID: {}", productId);
    }

    @Test
    void addImage() throws Exception {
        MockMultipartFile randomImageFile = testDataFactory.getRandomImageFile();

        MockHttpServletResponse imageUploadResponse = mockMvc.perform(multipart("/api/v1/images/")
                        .file(randomImageFile)
                        .headers(bearerTokenHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        ImageResponseDto imageResponseDto = objectMapper.readValue(imageUploadResponse.getContentAsString(), ImageResponseDto.class);

        assertThat(imageResponseDto)
                .isNotNull()
                .extracting(ImageResponseDto::getFileName, ImageResponseDto::getContentType)
                .containsExactly(randomImageFile.getOriginalFilename(), randomImageFile.getContentType());

        assertThat(imageResponseDto.getDownloadUrl()).isNotNull()
                .contains("download/");
    }

    @SneakyThrows
    @Test
    void addImagesToAProduct() {
        // Save a 'random' product first
        ResponseEntity<ProductResponseDto> productResponse = saveRandomProduct();

        assertNotNull(productResponse.getBody());
        Long productId = productResponse.getBody().getId();

        // Send multiple files to attach to the product
        MockHttpServletResponse imageUploadResponse = mockMvc.perform(multipart("/api/v1/images/product/{productId}", productId)
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .headers(bearerTokenHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Set<ImageResponseDto> uploadedImageResponses = objectMapper.readValue(imageUploadResponse.getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertAll(
                () -> assertNotNull(uploadedImageResponses),
                () -> assertEquals(3, uploadedImageResponses.size())
        );

    }

    private @NotNull ResponseEntity<ProductResponseDto> saveRandomProduct() {
        return restClient.post()
                .uri("/api/v1/products/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .body(testDataFactory.generateStreamOfProductRequests().findFirst().orElseThrow())
                .retrieve()
                .toEntity(ProductResponseDto.class);
    }

    @Test
    void getAllImages() {

        //Let's add a few images first
        saveFiveRandomImages();

        ResponseEntity<List<ImageResponseDto>> response = restClient.get()
                .uri("/api/v1/images/all")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertThat(response.getBody()).isNotNull();
        List<ImageResponseDto> allImages = response.getBody();

        Assertions.assertAll(
                () -> assertThat(allImages)
                        .hasSizeGreaterThanOrEqualTo(5)
        );
    }

    @SneakyThrows
    private MvcResult saveFiveRandomImages() {
        return mockMvc.perform(multipart("/api/v1/images/list")
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .headers(bearerTokenHeader))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void downloadImage() {

        saveFiveRandomImages();

        ResponseEntity<List<ImageResponseDto>> allImagesResponse = restClient.get()
                .uri("/api/v1/images/all")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allImagesResponse.getBody());
        Assertions.assertFalse(allImagesResponse.getBody().isEmpty());

        ImageResponseDto firstImage = allImagesResponse.getBody().getFirst();
        String downloadUrl = firstImage.getDownloadUrl();

        ResponseEntity<Resource> downloadResponse = restClient.get()
                .uri("/api/v1/images/%s".formatted(downloadUrl))
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(Resource.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, downloadResponse.getStatusCode()),
                () -> Assertions.assertNotNull(downloadResponse.getBody()),
                () -> Assertions.assertNotNull(downloadResponse.getHeaders().getContentType()),
                () -> Assertions.assertNotNull(downloadResponse.getHeaders().getContentDisposition()),
                () -> Assertions.assertEquals(firstImage.getFileName(), downloadResponse.getHeaders().getContentDisposition().getFilename())
        );
    }

    @SneakyThrows
    @Test
    void getImagesByProductId() {
        // Save a 'random' product first
        ResponseEntity<ProductResponseDto> productResponse = saveRandomProduct();

        assertNotNull(productResponse.getBody());
        Long productId = productResponse.getBody().getId();

        // Send multiple files to attach to the product
        MockHttpServletResponse imageUploadResponse = mockMvc.perform(multipart("/api/v1/images/product/{productId}", productId)
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .headers(bearerTokenHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Set<ImageResponseDto> uploadedImageResponses = objectMapper.readValue(imageUploadResponse.getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertAll(
                () -> assertThat(uploadedImageResponses).hasSize(3)
        );

        // Get images by product ID
        ResponseEntity<List<ImageResponseDto>> response = restClient.get()
                .uri("/api/v1/images/product/{productId}", productId)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(3, response.getBody().size());

        // Verify each image in the response
        for (ImageResponseDto image : response.getBody()) {
            Assertions.assertNotNull(image.getFileName());
            Assertions.assertNotNull(image.getContentType());
            Assertions.assertNotNull(image.getDownloadUrl());
        }
    }

    @SneakyThrows
    @Test
    void getImageByName() {
        // Save a 'random' product first
        MvcResult savedImagesResult = saveFiveRandomImages();

        Set<ImageResponseDto> uploadedImageResponses = objectMapper.readValue(savedImagesResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertAll(
                () -> assertThat(uploadedImageResponses).hasSizeGreaterThanOrEqualTo(5)
        );

        ImageResponseDto firstImage = uploadedImageResponses.stream().findFirst().orElseThrow();
        String fileName = firstImage.getFileName();

        ResponseEntity<Resource> downloadResponse = restClient.get()
                .uri("/api/v1/images/{imageName}", fileName)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(Resource.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, downloadResponse.getStatusCode()),
                () -> Assertions.assertNotNull(downloadResponse.getBody()),
                () -> Assertions.assertNotNull(downloadResponse.getHeaders().getContentType()),
                () -> Assertions.assertNotNull(downloadResponse.getHeaders().getContentDisposition()),
                () -> Assertions.assertEquals(firstImage.getFileName(), downloadResponse.getHeaders().getContentDisposition().getFilename())
        );


    }

    @SneakyThrows
    @Test
    void updateImage() {

        MvcResult savedImagesResult = saveFiveRandomImages();

        List<ImageResponseDto> savedImages = objectMapper.readValue(savedImagesResult.getResponse().getContentAsByteArray(), new TypeReference<>() {
        });

        ImageResponseDto firstSavedImage = savedImages.getFirst();

        String firstSavedImageId = firstSavedImage.getDownloadUrl().substring(firstSavedImage.getDownloadUrl().lastIndexOf("/") + 1);

        MockMultipartFile differentImage = getADifferentImage(firstSavedImage.getFileName());

        MockHttpServletResponse imageUploadResponse = mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/images/{id}", firstSavedImageId)
                        .file(differentImage)
                        .headers(bearerTokenHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        ImageResponseDto imageResponseDto = objectMapper.readValue(imageUploadResponse.getContentAsString(), ImageResponseDto.class);

        assertThat(imageResponseDto)
                .isNotNull()
                .extracting(ImageResponseDto::getFileName, ImageResponseDto::getContentType, ImageResponseDto::getDownloadUrl)
                .containsExactly(differentImage.getOriginalFilename(), differentImage.getContentType(), firstSavedImage.getDownloadUrl());
    }

    private @NotNull MockMultipartFile getADifferentImage(String fileName) {

        boolean isDifferentImage = false;

        while (!isDifferentImage) {
            MockMultipartFile randomImageFile = testDataFactory.getRandomImageFile();
            if (!randomImageFile.getOriginalFilename().equals(fileName)) {
                isDifferentImage = true;
                return randomImageFile;
            }
        }
        return null;
    }

    @SneakyThrows
    @Test
    void deleteImageById() {

        MvcResult savedImagesResult = saveFiveRandomImages();

        List<ImageResponseDto> savedImages = objectMapper.readValue(savedImagesResult.getResponse().getContentAsByteArray(), new TypeReference<>() {
        });

        ImageResponseDto firstSavedImage = savedImages.getFirst();

        String firstSavedImageId = firstSavedImage.getDownloadUrl().substring(firstSavedImage.getDownloadUrl().lastIndexOf("/") + 1);

        ResponseEntity<Void> imageDeletionResponse = restClient.delete()
                .uri("/api/v1/images/{id}", firstSavedImageId)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toBodilessEntity();

        Assertions.assertEquals(HttpStatus.NO_CONTENT, imageDeletionResponse.getStatusCode());

        RestClient.ResponseSpec responseSpec = restClient.get()
                .uri("/api/v1/images/%s".formatted(firstSavedImage.getDownloadUrl()))
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve();

        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> responseSpec.toEntity(Resource.class));
    }
}
