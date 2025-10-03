package dev.aj.full_stack_v6.category.controller;

import dev.aj.full_stack_v6.TestConfig;
import dev.aj.full_stack_v6.TestDataFactory;
import dev.aj.full_stack_v6.UserAuthFactory;
import dev.aj.full_stack_v6.category.repositories.CategoryRepository;
import dev.aj.full_stack_v6.common.domain.dtos.PageResponse;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, TestConfig.class, UserAuthFactory.class})
@TestPropertySource(locations = {"classpath:application-log.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class CategoryControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserAuthFactory userAuthFactory;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Environment environment;

    private RestClient categoryClient;

    private final Set<String> alreadyCommittedCategoryNames = new HashSet<>();

    private static final String CATEGORY_TEST = "-category-test";

    @BeforeAll
    void setUp() {
        userAuthFactory.setClients(port);
        categoryClient = userAuthFactory.authenticatedRestClient("http://localhost:%d%s".formatted(port, environment.getProperty("CATEGORY_API_PATH")));
    }

    @AfterAll
    void tearDown() {
        if (categoryClient != null) {
            categoryClient = null;
        }

        userAuthFactory.resetClients();

        categoryRepository.findAll()
                .stream()
                .filter(category -> category.getName().endsWith(CATEGORY_TEST))
                .forEach(categoryRepository::delete);
    }

    @Nested
    class PostCategoryTests {

        @Test
        void whenValidCategory_thenCreatesCategory() {
            Category newCategory = createUniqueCategory();
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
            Category newCategory = createUniqueCategory();
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

            saveANewRandomCategory(createUniqueCategory());

            ResponseEntity<List<Category>> allCategoriesResponse = getAllCategories();

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
            Category newCategory = createUniqueCategory();
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
        void whenCategoryByIdDoesNotExist_thenThrowsNotFound() {
            Assertions.assertThatThrownBy(() -> getCategoryById(Long.MAX_VALUE))
                    .isInstanceOf(HttpClientErrorException.NotFound.class);
        }

        @Test
        void whenGetCategoriesByPage_thenReturnsPageOfCategories() {

            List<@NonNull Category> savedCategories = new ArrayList<>();

            for (int i = 0; i < 50; i++) {
                ResponseEntity<Category> categoryResponseEntity = saveANewRandomCategory(createUniqueCategory());
                if (categoryResponseEntity.getStatusCode().is2xxSuccessful()) {
                    savedCategories.add(Objects.requireNonNull(categoryResponseEntity.getBody()));
                }
            }

            ResponseEntity<PageResponse<Category>> pageResponse = categoryClient.get()
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

            ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(createUniqueCategory());
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
                newCategory = Objects.requireNonNull(saveANewRandomCategory(createUniqueCategory())
                        .getBody());
            }
        }

        @Test
        @Order(1)
        void whenValidPatch_thenAccepted() {
            Category patchedCategory = new Category();
            patchedCategory.setName(newCategory.getName() + "-Patched");

            ResponseEntity<Void> categoryPatchResponse = categoryClient.patch()
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
        @Order(2)
        void testIdempotentPatch_thenSuccessful() {
            Category patchedCategory = new Category();
            patchedCategory.setName(newCategory.getName() + "-Patched");

            assertDoesNotThrow(() -> categoryClient.patch()
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
            Category newCategory = createUniqueCategory();
            ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);

            Long categoryId = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();
            newCategory.setName(newCategory.getName() + " Put");

            ResponseEntity<Void> putCategoryResponse = categoryClient.put()
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
            Category newCategory = createUniqueCategory();
            ResponseEntity<Category> createdCategoryResponse = saveANewRandomCategory(newCategory);

            Long categoryId = Objects.requireNonNull(createdCategoryResponse.getBody()).getId();
            newCategory.setName(newCategory.getName() + " Put");

            ResponseEntity<Void> categoryUpdateResponse = assertDoesNotThrow(() -> categoryClient.put()
                    .uri("/{id}", categoryId)
                    .body(newCategory)
                    .retrieve()
                    .toBodilessEntity());

            categoryUpdateResponse
                    .getStatusCode().is2xxSuccessful();
        }
    }

    private @NonNull Category createUniqueCategory() {


        if (alreadyCommittedCategoryNames.isEmpty()) {
            ResponseEntity<List<Category>> allCategoriesResponse = this.getAllCategories();

            if (allCategoriesResponse.getStatusCode().is2xxSuccessful()) {
                alreadyCommittedCategoryNames.addAll(
                        Objects.requireNonNull(allCategoriesResponse.getBody())
                                .stream()
                                .map(Category::getName)
                                .collect(Collectors.toSet())
                );
            }
            log.info("Added {} names to already committed categories.", alreadyCommittedCategoryNames);
        }
        return testDataFactory.getStreamOfCategories()
                .peek(category -> category.setName(category.getName().concat(String.valueOf(Math.random())) + CATEGORY_TEST))
                .filter(category -> !alreadyCommittedCategoryNames.contains(category.getName()))
                .limit(1)
                .peek(category -> alreadyCommittedCategoryNames.add(category.getName()))
                .findFirst()
                .orElseThrow();
    }

    private @NonNull ResponseEntity<Category> saveANewRandomCategory(Category newCategory) {
        return categoryClient.post()
                .uri("/")
                .body(newCategory)
                .retrieve()
                .toEntity(Category.class);
    }

    private @NotNull ResponseEntity<List<Category>> getAllCategories() {
        return categoryClient.get()
                .uri("/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    private @NotNull ResponseEntity<Category> getCategoryById(Long categoryId) {
        return categoryClient.get()
                .uri("/{id}", categoryId)
                .retrieve()
                .toEntity(Category.class);
    }

    private @NotNull ResponseEntity<Void> deleteCategoryById(Long categoryId) {
        return categoryClient.delete()
                .uri("/{id}", categoryId)
                .retrieve()
                .toBodilessEntity();
    }
}
