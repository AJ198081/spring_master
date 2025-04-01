package dev.aj.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import com.fasterxml.jackson.core.type.TypeReference;
import dev.aj.elasticsearch.ESTCContainerConfig;
import dev.aj.elasticsearch.TestConfig;
import dev.aj.elasticsearch.TestData;
import dev.aj.elasticsearch.domain.Product;
import dev.aj.elasticsearch.repositories.ProductESRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import(value = {ESTCContainerConfig.class, TestData.class, TestConfig.class})
@Import(value = {TestData.class, TestConfig.class})

@TestPropertySource(locations = "classpath:application-test.properties", properties = {
//        "logging.level.root=off",
//        "spring.docker.compose.enabled=false",
        "logging.level.dev.aj.elasticsearch=debug"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ProductServiceTest {

    public static final String SAMPLE_DATA_FILE_NAME = "sampleProducts.json";
    public static final int SAMPLE_PRODUCT_SIZE = 1000;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductESRepository productESRepository;

    @Autowired
    private TestData testData;

    @BeforeAll
    void beforeAll() {
        productESRepository.deleteAll();
        List<Product> sampleProducts = testData.getStreamOfProducts().limit(SAMPLE_PRODUCT_SIZE).toList();
        testData.writeResource(SAMPLE_DATA_FILE_NAME, sampleProducts);
    }

    @AfterAll
    void afterAll() {
        testData.deleteTestDataFile(SAMPLE_DATA_FILE_NAME);
    }

    @Test
    void readProductsFromJson() {
        List<Product> products = testData.readResource(SAMPLE_DATA_FILE_NAME, new TypeReference<>() {
        });
        Assertions.assertNotNull(products);
        Assertions.assertEquals(SAMPLE_PRODUCT_SIZE, products.size());

        List<Product> persistedProducts = productService.saveAll(products);

        Assertions.assertNotNull(persistedProducts);
        Assertions.assertEquals(SAMPLE_PRODUCT_SIZE, persistedProducts.size());

        List<Product> productsOfBranch = productService.findByBrand(products.getFirst().getBrand());

        Assertions.assertNotNull(productsOfBranch);
        org.assertj.core.api.Assertions.assertThat(productsOfBranch)
                .hasSizeGreaterThanOrEqualTo(0);
    }

    @Test
    void findProductsByNameTextType() {
        List<Product> sampleProducts = testData.getStreamOfProducts().limit(SAMPLE_PRODUCT_SIZE).toList();

        List<Product> savedSampleProducts = productService.saveAll(sampleProducts);

        org.assertj.core.api.Assertions.assertThat(savedSampleProducts).isNotNull()
                .extracting("name")
                .satisfies(sampleProducts::containsAll);

        Product firstSavedProduct = savedSampleProducts.getFirst();

        String[] nameWords = firstSavedProduct.getName().split("\\s");

        String middleNameWord = nameWords[nameWords.length / 2];

        List<Product> listOfProducts = productService.findByName(middleNameWord);

        Long totalProductCount = productService.count();
        log.info("Total products in database: {}", totalProductCount);

        org.assertj.core.api.Assertions.assertThat(listOfProducts).isNotNull()
                .extracting("name")
                .contains(firstSavedProduct.getName());
    }

    @Test
    void findProductPagesByCategory() {
        List<Product> sampleProducts = testData.getStreamOfProducts().limit(SAMPLE_PRODUCT_SIZE).toList();

        List<Product> savedSampleProducts = productService.saveAll(sampleProducts);

        org.assertj.core.api.Assertions.assertThat(savedSampleProducts).isNotNull()
                .extracting("name")
                .satisfies(sampleProducts::containsAll);

        Product firstSavedProduct = savedSampleProducts.getFirst();

        String firstSavedProductCategory = firstSavedProduct.getCategory();

        List<Product> listOfProducts = productService.findByCategory(firstSavedProductCategory, 0, 5000);

        log.info("Total products in the database: {}", productService.count());

        org.assertj.core.api.Assertions.assertThat(listOfProducts).isNotNull()
                .extracting("name")
                .contains(firstSavedProduct.getName());
    }

    @Test
    void testProductSearchQuery() {
        List<Product> sampleProducts = testData.getStreamOfProducts().limit(SAMPLE_PRODUCT_SIZE).toList();

        List<Product> savedSampleProducts = productService.saveAll(sampleProducts);

        Product firstProduct = savedSampleProducts.getFirst();

        List<Product> productsSearchedByQuery = productService.productSearchQuery(
                firstProduct.getBrand(),
                firstProduct.getCategory(),
                firstProduct.getPrice().min(BigDecimal.ONE).doubleValue(),
                firstProduct.getPrice().add(BigDecimal.ONE).doubleValue()
        );

        productsSearchedByQuery.forEach(product -> log.info("Product: {}", product));

        log.info("Total products currently in the database: {}", productService.count());
    }

    @Test
    void testProductAggregation() {
        List<Product> sampleProducts = testData.getStreamOfProducts().limit(SAMPLE_PRODUCT_SIZE).toList();
        productService.saveAll(sampleProducts);

        Map<String, Aggregate> productAggregations = productService.productAggregations();
        productAggregations.forEach((key, value) -> log.info("{}: {}", key, value));

        log.info("Total number of products currently in the database: {}", productService.count());
    }

    @Test
    @Disabled(value = "Need to sort the mapping out for @CompletionContext, better use a JSON mapping to set the indexes up")
    void suggestionsAsYouTypeName() {
        List<Product> sampleProducts = testData.getStreamOfProducts().limit(SAMPLE_PRODUCT_SIZE).toList();
        productService.saveAll(sampleProducts);

        Set<String> nameSuggestions = productService.suggestionsAsYouTypeName(sampleProducts.getFirst().getName().split("\\s")[0]);

        org.assertj.core.api.Assertions.assertThat(nameSuggestions).isNotNull()
                .hasSizeGreaterThanOrEqualTo(1)
                .allSatisfy(suggestion -> sampleProducts.stream().map(Product::getName).toList().contains(suggestion));

    }
}