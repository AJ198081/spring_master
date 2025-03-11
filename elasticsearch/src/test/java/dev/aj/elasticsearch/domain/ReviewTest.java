package dev.aj.elasticsearch.domain;

import dev.aj.elasticsearch.ESTCContainerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.index.Settings;

import java.time.Instant;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ESTCContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ReviewTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeAll
    void beforeAll() {
        boolean indexDeleted = elasticsearchOperations.indexOps(Review.class).delete();
        log.info("Review index deleted: {}", indexDeleted);
    }

    @Test
    void testIndexCreation() {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(Review.class);

        if (!indexOperations.exists()) {
            log.info("Review index does not exist, creating...");
            indexOperations.createWithMapping();
        }

        Settings indexSettings = indexOperations.getSettings();

        log.info("Index created: {}, with shards: {}, replicas: {}, created at: {}",
                indexSettings.get("index.provided_name"),
                indexSettings.get("index.number_of_shards"),
                indexSettings.get("index.number_of_replicas"),
                Date.from(Instant.ofEpochMilli((Long) indexSettings.get("index.creation_date"))).toInstant());

        log.info("Index mapping: {}", indexOperations.getMapping());
    }

}