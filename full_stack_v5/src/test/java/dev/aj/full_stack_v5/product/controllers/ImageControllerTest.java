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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
//@Disabled("This test is disabled, pending troubleshooting of restClient and Multipart file submission")
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
                .contains("/download/");
    }

    @SneakyThrows
    @Test
    void addImagesToAProduct() {
        // Create a product first
        ResponseEntity<ProductResponseDto> productResponse = restClient.post()
                .uri("/api/v1/products/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .body(testDataFactory.generateStreamOfProductRequests().findFirst().orElseThrow())
                .retrieve()
                .toEntity(ProductResponseDto.class);

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
        
        Set<ImageResponseDto> responseDtos = objectMapper.readValue(imageUploadResponse.getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertAll(
                () -> assertNotNull(responseDtos),
                () -> assertEquals(3, responseDtos.size())
        );

    }

    @SneakyThrows
    @Test
    void getAllImages() {

        mockMvc.perform(multipart("/api/v1/images/list")
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .file(testDataFactory.getRandomImageFile())
                        .headers(bearerTokenHeader))
                .andExpect(status().isOk());
        
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
                        .hasSizeGreaterThanOrEqualTo(5),
                () -> assertThat(allImages).filteredOn(image -> !image.getDownloadUrl().startsWith("/download/"))
                        .isEmpty()
        );
    }

    @Test
    void downloadImage() {
        // Get all images first
        ResponseEntity<List<ImageResponseDto>> allImagesResponse = restClient.get()
                .uri("/api/v1/images/all")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allImagesResponse.getBody());
        Assertions.assertFalse(allImagesResponse.getBody().isEmpty());

        // Get the first image
        ImageResponseDto firstImage = allImagesResponse.getBody().getFirst();
        String downloadUrl = firstImage.getDownloadUrl();

        // Extract the ID from the download URL
        Long imageId = Long.parseLong(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1));

        // Download the image
        ResponseEntity<Resource> downloadResponse = restClient.get()
                .uri("/api/v1/images/download/{id}", imageId)
                .retrieve()
                .toEntity(Resource.class);

        Assertions.assertEquals(HttpStatus.OK, downloadResponse.getStatusCode());
        Assertions.assertNotNull(downloadResponse.getBody());
        Assertions.assertNotNull(downloadResponse.getHeaders().getContentType());
        Assertions.assertNotNull(downloadResponse.getHeaders().getContentDisposition());
    }

    @Test
    void getImagesByProductId() {
        // Create a product first
        ResponseEntity<ProductResponseDto> productResponse = restClient.post()
                .uri("/api/v1/products/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(testDataFactory.generateStreamOfProductRequests().findFirst().orElseThrow())
                .retrieve()
                .toEntity(ProductResponseDto.class);

        assertNotNull(productResponse.getBody());
        Long productId = productResponse.getBody().getId();

        // Add images to the product
        var imageRequests = testDataFactory.generateStreamOfImages()
                .limit(3)
                .toList();

        restClient.post()
                .uri("/api/v1/images/product/{productId}", productId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(imageRequests)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Set<ImageResponseDto>>() {
                });

        // Get images by product ID
        ResponseEntity<List<ImageResponseDto>> response = restClient.get()
                .uri("/api/v1/images/product/{productId}", productId)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
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

    @Test
    void updateImage() {
        // Get all images first
        ResponseEntity<List<ImageResponseDto>> allImagesResponse = restClient.get()
                .uri("/api/v1/images/all")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertNotNull(allImagesResponse.getBody());
        Assertions.assertFalse(allImagesResponse.getBody().isEmpty());

        // Get the first image
        ImageResponseDto firstImage = allImagesResponse.getBody().getFirst();
        String downloadUrl = firstImage.getDownloadUrl();

        // Extract the ID from the download URL
        Long imageId = Long.parseLong(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1));

        // Create a new image request
        var updatedImageRequest = testDataFactory.generateStreamOfImages()
                .findFirst()
                .orElseThrow();

        // Update the image
        ResponseEntity<ImageResponseDto> updateResponse = restClient.put()
                .uri("/api/v1/images/{imageId}", imageId)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(updatedImageRequest)
                .retrieve()
                .toEntity(ImageResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Assertions.assertNotNull(updateResponse.getBody());
        Assertions.assertNotNull(updateResponse.getBody().getFileName());
        Assertions.assertNotNull(updateResponse.getBody().getContentType());
        Assertions.assertNotNull(updateResponse.getBody().getDownloadUrl());
    }

    @Test
    void deleteImageById() {
        // Add a new image first
        var imageRequest = testDataFactory.generateStreamOfImages()
                .findFirst()
                .orElseThrow();

        ResponseEntity<ImageResponseDto> addResponse = restClient.post()
                .uri("/api/v1/images/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(imageRequest)
                .retrieve()
                .toEntity(ImageResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        Assertions.assertNotNull(addResponse.getBody());

        // Extract the ID from the download URL
        String downloadUrl = addResponse.getBody().getDownloadUrl();
        Long imageId = Long.parseLong(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1));

        // Delete the image
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/api/v1/images/delete?id={id}", imageId)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(Void.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Try to download the deleted image, should throw an exception
        RestClient.ResponseSpec responseSpec = restClient.get()
                .uri("/api/v1/images/download/{id}", imageId)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve();

        Assertions.assertThrows(Exception.class, () -> responseSpec.toEntity(Resource.class));
    }
}
