package dev.aj.elasticsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.index.Settings;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchIndexService {

    private final ElasticsearchOperations elasticsearchOperations;

    public Settings createIndex(String indexName) {

        IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
        if (!indexOperations.exists()) {
            indexOperations.create();
        }

        return indexOperations.getSettings();
    }

}
