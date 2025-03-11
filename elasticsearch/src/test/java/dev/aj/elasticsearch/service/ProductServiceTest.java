package dev.aj.elasticsearch.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {ESTCContainerConfig.class, TestData.class, TestConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties", properties = {
        "logging.level.root=off"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ProductServiceTest {

    public static final String SAMPLE_DATA_FILE_NAME = "sampleProducts.json";
    public static final int SAMPLE_PRODUCT_SIZE = 10;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductESRepository producRepository;
    @Autowired
    private TestData testData;

    @BeforeAll
    void beforeAll() {
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
}