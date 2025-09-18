package dev.aj.full_stack_v6.category.controller;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.category.repositories.CategoryRepository;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    private Set<String> alreadyCommittedCategoryNames = new HashSet<>();

    @BeforeEach
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d%s".formatted(port, CATEGORY_CONTROLLER_BASE_PATH));
        categoryRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        if (restClient != null) {
            restClient = null;
        }
    }

    @Nested
    class SaveCategoryTests {

        @Test
        void whenValidCategory_thenCreatesCategory() {
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
        void whenDuplicateName_thenReturnsConflict() {
            Category newCategory = createSampleCategory();
            saveANewRandomCategory(newCategory);

            Assertions.assertThatThrownBy(() -> saveANewRandomCategory(newCategory))
                    .isInstanceOf(HttpClientErrorException.Conflict.class);
        }

        @Test
        void whenInvalidPayload_thenReturnsBadRequest() {
            Assertions.assertThatThrownBy(() -> saveANewRandomCategory(new Category()))
                    .isInstanceOf(HttpClientErrorException.BadRequest.class);

            HttpClientErrorException.BadRequest badRequest =
                    assertThrows(HttpClientErrorException.BadRequest.class, () -> saveANewRandomCategory(new Category()));

            Assertions.assertThat(badRequest.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            Assertions.assertThat(badRequest.getResponseBodyAsString()).contains("category.name");
            Assertions.assertThat(badRequest.getResponseBodyAsString()).contains("must not be empty");
            Assertions.assertThat(badRequest.getResponseBodyAsString()).contains("must not be null");
        }
    }

    @Nested
    class GetCategoryTests {

        @Test
        void getAllCategories_thenReturnsCategories() {

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

        @Test
        void whenCategoryByIdExists_thenReturnsCategory() {
            Category newCategory = createSampleCategory();
            ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);
            Long categoryId = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();

            ResponseEntity<Category> categoryResponse = getCategoryById(categoryId);
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
        void whenCategoryByIdDoesNotExist_thenReturnsNotFound() {
            Assertions.assertThatThrownBy(() -> getCategoryById(Long.MAX_VALUE))
                    .isInstanceOf(HttpClientErrorException.NotFound.class);
        }

        @Test
        void whenGetCategoriesByPage_thenReturnsPageOfCategories() {

            List<@NonNull Category> savedCategories = new ArrayList<>();

            for (int i = 0; i < 100; i++) {
                ResponseEntity<Category> categoryResponseEntity = saveANewRandomCategory(createSampleCategory());
                if (categoryResponseEntity.getStatusCode().is2xxSuccessful()) {
                    savedCategories.add(Objects.requireNonNull(categoryResponseEntity.getBody()));
                }
            }



            ResponseEntity<PageResponse<Category>> pageResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/page")
                            .queryParam("name", savedCategories.getFirst().getName().split("")[0])
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .queryParam("sortDirection", "asc")
                            .build())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            Assertions.assertThat(pageResponse)
                    .isNotNull()
                    .satisfies(response -> {
                        Assertions.assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.OK);
                        Assertions.assertThat(response.getBody())
                                .isNotNull()
                                .satisfies(categories -> Assertions.assertThat(categories)
                                        .extracting(PageResponse::content)
                                        .asInstanceOf(InstanceOfAssertFactories.LIST)
                                        .hasSizeLessThanOrEqualTo(10)
                                        .hasSizeGreaterThanOrEqualTo(1));
                    });

        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteCategoryTests {

        Long categoryId;

        @Test
        @Order(1)
        void deleteCategoryById_Successful() {

            ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(createSampleCategory());
            Long id = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();
            categoryId = String.valueOf(id).isBlank() ? id : Long.parseLong(String.valueOf(id));

            ResponseEntity<Void> categoryDeletionResponse = deleteCategoryById(categoryId);

            Assertions.assertThat(categoryDeletionResponse)
                    .isNotNull()
                    .satisfies(response -> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK));
        }

        @Test
        void deleteCategoryByNonExistenceId_thenIdempotent() {
            assertDoesNotThrow(() -> deleteCategoryById(categoryId))
                    .getStatusCode().is2xxSuccessful();
        }

        @Test
        void deleteCategoryById_whenCategoryDoesNotExist_thenReturnsSuccessful() {
            assertDoesNotThrow(() -> deleteCategoryById(Long.MAX_VALUE))
                    .getStatusCode().is2xxSuccessful();
        }
    }

    @Nested
    class PatchCategoryTests {

        private Category newCategory;

        @BeforeEach
        void beforeEach() {
            if (newCategory == null) {
                newCategory = Objects.requireNonNull(saveANewRandomCategory(createSampleCategory())
                        .getBody());
            }
        }

        @Test
        void whenValidPatch_thenAccepted() {
            Category patchedCategory = new Category();
            patchedCategory.setName(newCategory.getName() + " Patched");

            ResponseEntity<Void> categoryPatchResponse = restClient.patch()
                    .uri("/{id}", newCategory.getId())
                    .body(patchedCategory)
                    .retrieve()
                    .toBodilessEntity();

            Assertions.assertThat(categoryPatchResponse)
                    .isNotNull()
                    .satisfies(response ->
                            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED));
        }

        @Test
        void testIdempotentPatch_thenSuccessful() {
            Category patchedCategory = new Category();
            patchedCategory.setName(newCategory.getName() + " Patched");

            assertDoesNotThrow(() -> restClient.patch()
                    .uri("/{id}", newCategory.getId())
                    .body(patchedCategory)
                    .retrieve()
                    .toBodilessEntity())
                    .getStatusCode().is2xxSuccessful();
        }
    }

    @Nested
    class PutCategoryTests {

        @Test
        void whenValidUpdate_thenAccepted() {
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
                    .satisfies(response ->
                            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED)
                    );
        }

        @Test
        void whenDuplicateCategoryNameUpdate_thenReturnsConflict() {
            Category newCategory = createSampleCategory();
            ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);

            Long categoryId = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();
            newCategory.setName(newCategory.getName() + " Put");

            ResponseEntity<Void> categoryUpdateResponse = assertDoesNotThrow(() -> restClient.put()
                    .uri("/{id}", categoryId)
                    .body(newCategory)
                    .retrieve()
                    .toBodilessEntity());

            categoryUpdateResponse
                    .getStatusCode().is2xxSuccessful();
        }
    }

    private @NonNull Category createSampleCategory() {
        ResponseEntity<List<Category>> allCategoriesResponse = this.getAllCategoriesResponse();
        if (allCategoriesResponse.getStatusCode().is2xxSuccessful()) {
            alreadyCommittedCategoryNames = Objects.requireNonNull(allCategoriesResponse.getBody())
                    .stream()
                    .map(Category::getName)
                    .collect(Collectors.toSet());
        }
        return testDataFactory.getStreamOfCategories()
                .filter(category -> !alreadyCommittedCategoryNames.contains(category.getName()))
                .limit(1)
                .peek(category -> alreadyCommittedCategoryNames.add(category.getName()))
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

    private @NotNull ResponseEntity<List<Category>> getAllCategoriesResponse() {
        return restClient.get()
                .uri("/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    private @NotNull ResponseEntity<Category> getCategoryById(Long categoryId) {
        return restClient.get()
                .uri("/{id}", categoryId)
                .retrieve()
                .toEntity(Category.class);
    }

    private @NotNull ResponseEntity<Void> deleteCategoryById(Long categoryId) {
        return restClient.delete()
                .uri("/{id}", categoryId)
                .retrieve()
                .toBodilessEntity();
    }

    record PageResponse<T>(
            List<T> content,
            int number,
            int size,
            long totalElements,
            int totalPages,
            int numberOfElements,
            boolean first,
            boolean last,
            boolean empty,
            Map<String, Object> sort,
            Map<String, Object> pageable
    ) {
    }

}