package dev.aj.elasticsearch.service;


import dev.aj.elasticsearch.ESTCContainerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.index.Settings;

import java.time.Instant;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ESTCContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class ElasticsearchIndexServiceTest {

    @Autowired
    private ElasticsearchIndexService elasticsearchIndexService;

    @Test
    public void testIndexCreation() {
        Settings indexSettings = elasticsearchIndexService.createIndex("test_index");
        log.info("Index created: {}, with shards: {}, replicas: {}, created at: {}",
                indexSettings.get("index.provided_name"),
                indexSettings.get("index.number_of_shards"),
                indexSettings.get("index.number_of_replicas"),
                Date.from(Instant.ofEpochMilli((Long) indexSettings.get("index.creation_date"))).toInstant());
    }


}
