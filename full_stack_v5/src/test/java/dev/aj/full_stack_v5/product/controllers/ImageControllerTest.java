package dev.aj.full_stack_v5.product.controllers;

import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageResponseDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@Disabled("This test is disabled, pending troubleshooting of restClient and Multipart file submission")
class ImageControllerTest {


    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private PhotosFactory photosFactory;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));

        // Create a product first to associate images with
        ResponseEntity<ProductResponseDto> productResponse = restClient.post()
                .uri("/api/v1/products/")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(testDataFactory.generateStreamOfProductRequests().findFirst().orElseThrow())
                .retrieve()
                .toEntity(ProductResponseDto.class);

        assertNotNull(productResponse.getBody());
        Long productId = productResponse.getBody().getId();
        log.info("Created product with ID: {}", productId);

        // Add images to the product
/*        testDataFactory.generateStreamOfImages()
                .limit(5)
                .forEach(image -> {
                    // Create a MultiValueMap to hold the file
                    org.springframework.util.LinkedMultiValueMap<String, Object> map = new org.springframework.util.LinkedMultiValueMap<>();
                    map.add("file", image.getFile());

                    ResponseEntity<ImageResponseDto> imageResponse = restClient.post()
                            .uri("/api/v1/images/")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                            .body(map)
                            .retrieve()
                            .toEntity(ImageResponseDto.class);

                    log.info("Added image: {}", imageResponse.getBody());
                });*/
    }

    @Test
    void addImage() {
        var imageRequestDto = testDataFactory.generateStreamOfImages()
                .findFirst()
                .orElseThrow();

        ResponseEntity<ImageResponseDto> response = restClient.post()
                .uri("/api/v1/images/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(imageRequestDto.getFile())
                .retrieve()
                .toEntity(ImageResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getFileName());
        Assertions.assertNotNull(response.getBody().getContentType());
        Assertions.assertNotNull(response.getBody().getDownloadUrl());
    }

    @Test
    void addImagesToAProduct() {
        // Create a product first
        ResponseEntity<ProductResponseDto> productResponse = restClient.post()
                .uri("/api/v1/products/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(testDataFactory.generateStreamOfProductRequests().findFirst().orElseThrow())
                .retrieve()
                .toEntity(ProductResponseDto.class);

        assertNotNull(productResponse.getBody());
        Long productId = productResponse.getBody().getId();

        // Create a list of image requests
        List<ImageRequestDto> imageRequests = testDataFactory.generateStreamOfImages()
                .limit(3)
                .toList();

        // Create a MultiValueMap to hold the files
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        imageRequests.forEach(imageRequest -> map.add("files", imageRequest.getFile()));

        // Add images to the product
        ResponseEntity<Set<ImageResponseDto>> response = restClient.post()
                .uri("/api/v1/images/product/{productId}", productId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(map)
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
    void getAllImages() {
        ResponseEntity<List<ImageResponseDto>> response = restClient.get()
                .uri("/api/v1/images/all")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());

        // We added at least 5 images in setup, plus one in the addImage test, plus 3 in addImagesToAProduct
        Assertions.assertTrue(response.getBody().size() >= 5);

        // Verify each image in the response
        for (ImageResponseDto image : response.getBody()) {
            Assertions.assertNotNull(image.getFileName());
            Assertions.assertNotNull(image.getContentType());
            Assertions.assertNotNull(image.getDownloadUrl());
        }
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
        List<ImageRequestDto> imageRequests = testDataFactory.generateStreamOfImages()
                .limit(3)
                .collect(Collectors.toList());

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
        ImageRequestDto updatedImageRequest = testDataFactory.generateStreamOfImages()
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
        ImageRequestDto imageRequest = testDataFactory.generateStreamOfImages()
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
