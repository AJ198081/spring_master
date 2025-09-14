package dev.aj.full_stack_v6.category.controller;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.category.repositories.CategoryRepository;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, TestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CategoryControllerTest {

    private static final String CATEGORY_CONTROLLER_BASE_PATH = "/api/v1/categories";

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private CategoryRepository categoryRepository;

    private RestClient restClient;

    private List<String> existingCategoryName;

    @BeforeEach
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d%s"
                .formatted(port, CATEGORY_CONTROLLER_BASE_PATH));

        existingCategoryName = Objects.requireNonNull(getAllCategoriesResponse().getBody())
                .stream()
                .map(Category::getName)
                .toList();
    }

    @AfterEach
    void tearDown() {
        if (restClient != null) {
            restClient = null;
        }
    }

    @Test
    void saveCategory() {

        Category newCategory = createSampleCategory();
        ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);

        Assertions.assertThat(createdCategoryResponse)
                .isNotNull()
                .satisfies(response -> {
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    Assertions.assertThat(response.getBody())
                            .isNotNull()
                            .satisfies(category -> Assertions.assertThat(category.getName())
                                    .isNotNull()
                                    .isEqualTo(newCategory.getName()));
                });

    }

    @Test
    void getAllCategories() {

        saveANewRandomCategory(createSampleCategory());

        ResponseEntity<List<Category>> allCategoriesResponse = getAllCategoriesResponse();

        log.info("All Categories: {}", allCategoriesResponse.getBody());

        Assertions.assertThat(allCategoriesResponse)
                .isNotNull()
                .satisfies(response -> {
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    Assertions.assertThat(response.getBody())
                            .isNotNull()
                            .isNotEmpty();
                });
    }

    private @NotNull ResponseEntity<List<Category>> getAllCategoriesResponse() {
        return restClient.get()
                .uri("/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    @Test
    void getCategoryById() {
        Category newCategory = createSampleCategory();
        ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);
        Long categoryId = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();

        ResponseEntity<Category> categoryResponse = restClient.get()
                .uri("/{id}", categoryId)
                .retrieve()
                .toEntity(Category.class);
        Assertions.assertThat(categoryResponse)
                .isNotNull()
                .satisfies(response -> {
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    Assertions.assertThat(response.getBody())
                            .isNotNull()
                            .satisfies(category -> Assertions.assertThat(category.getId()).isEqualTo(categoryId));
                });
    }

    @Test
    void deleteCategoryById() {

        Category newCategory = createSampleCategory();
        ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);

        Long categoryId = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();

        ResponseEntity<Void> categoryDeletionResponse = deleteCategoryById(categoryId);

        Assertions.assertThat(categoryDeletionResponse)
                .isNotNull()
                .satisfies(response -> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK));

        org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> deleteCategoryById(categoryId));
    }

    @Test
    void patchCategory() {
        Category newCategory = createSampleCategory();
        ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);

        Long categoryId = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();

        newCategory.setName(newCategory.getName() + " Patched");

        ResponseEntity<Void> categoryUpdateResponse = restClient.patch()
                .uri("/{id}", categoryId)
                .body(newCategory)
                .retrieve()
                .toBodilessEntity();

        Assertions.assertThat(categoryUpdateResponse)
                .isNotNull()
                .satisfies(response -> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));
    }

    @Test
    void putCategory() {
        Category newCategory = createSampleCategory();
        ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);

        Long categoryId = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();
        newCategory.setName(newCategory.getName() + " Put");

        ResponseEntity<Void> putCategoryResponse = restClient.put()
                .uri("/{id}", categoryId)
                .body(newCategory)
                .retrieve()
                .toBodilessEntity();

        Assertions.assertThat(putCategoryResponse)
                .isNotNull()
                .satisfies(response -> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));

        org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.Conflict.class, () -> restClient.put()
                .uri("/{id}", categoryId)
                .body(newCategory)
                .retrieve()
                .toBodilessEntity());


    }

    private @NotNull ResponseEntity<Void> deleteCategoryById(Long categoryId) {
        return restClient.delete()
                .uri("/{id}", categoryId)
                .retrieve()
                .toBodilessEntity();
    }

    private @NonNull Category createSampleCategory() {
        return testDataFactory.getStreamOfCategories()
                .limit(1)
                .findFirst()
                .orElseThrow();
    }

    private @NonNull ResponseEntity<Category> saveANewRandomCategory(Category newCategory) {
        return restClient.post()
                .uri("/")
                .body(newCategory)
                .retrieve()
                .toEntity(Category.class);
    }
}