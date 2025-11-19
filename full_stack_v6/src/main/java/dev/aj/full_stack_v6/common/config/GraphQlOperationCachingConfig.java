package dev.aj.full_stack_v6.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class GraphQlOperationCachingConfig {
    public static final String GRAPHQL_PARSED_QUERY_CACHE = "graphql-parsed-query-cache";

    /*private final Map<String, PreparsedDocumentEntry> queryCache = new ConcurrentHashMap<>();

    @Bean
    public PreparsedDocumentProvider preparsedDocumentProvider() {
        return ((executionInput, parseAndValidateFunction) -> {
            queryCache.computeIfAbsent(executionInput.getQuery(), key -> {
                log.info("Query: {} not found in cache, parsing and validating", executionInput.getQuery());
                return parseAndValidateFunction.apply(executionInput);
            });

            log.info("Getting Query: {} from the cache", executionInput.getQuery());
            return CompletableFuture.completedFuture(queryCache.get(executionInput.getQuery()));
        });
    }*/

    @Bean
    public PreparsedDocumentProvider preparsedDocumentProvider(CacheManager cacheManager) {
        return ((executionInput, parseAndValidateFunction) -> {

            Cache cache = Objects.requireNonNull(cacheManager.getCache(GRAPHQL_PARSED_QUERY_CACHE), "Cache not found");

            PreparsedDocumentEntry cachedDocument = cache.get(executionInput.getQuery(), PreparsedDocumentEntry.class);

            if (cachedDocument == null) {
                cachedDocument = parseAndValidateFunction.apply(executionInput);
                cache.put(executionInput.getQuery(), cachedDocument);
            }

            log.info("Getting Query: {} from the cache", executionInput.getQuery());
            return CompletableFuture.completedFuture(cachedDocument);
        });
    }


    @Bean
    public GraphQlSourceBuilderCustomizer graphQlSourceBuilderCustomizer(PreparsedDocumentProvider preparsedDocumentProvider) {
        return configurer -> configurer.configureGraphQl(builder ->
                builder.preparsedDocumentProvider(preparsedDocumentProvider));
    }

    @Bean
    public Caffeine<@NonNull Object, @NonNull Object> caffeineCacheProperties() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.DAYS);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<@NonNull Object, @NonNull Object> caffeineCacheProperties) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(GRAPHQL_PARSED_QUERY_CACHE);
        caffeineCacheManager.setCaffeine(caffeineCacheProperties);
        return caffeineCacheManager;
    }

}
