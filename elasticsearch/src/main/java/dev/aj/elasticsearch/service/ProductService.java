package dev.aj.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.RangeAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StatsAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.elasticsearch.domain.Product;
import dev.aj.elasticsearch.repositories.ProductESRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductESRepository productESRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;


    public List<Product> saveAll(List<Product> products) {
        return Streamable.of(productESRepository.saveAll(products)).toList();
    }

    public List<Product> findByCategory(String category) {
        SearchHits<Product> foundProducts = productESRepository.findByCategory(category);

        return foundProducts.map(searchHit -> objectMapper.convertValue(searchHit.getContent(), Product.class))
                .toList();
    }

    public List<Product> findByBrand(String brandName) {

        return productESRepository.findByBrand(brandName, PageRequest.of(0, 100))
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    public List<Product> findByName(String queryName) {
        SearchHits<Product> productSearchHits = productESRepository.findByName(queryName);
        return productSearchHits.map(SearchHit::getContent).toList();
    }

    public List<Product> findPageByName(String name, int page, int size) {
        SearchPage<Product> searchPage = productESRepository.findByBrand(name, PageRequest.of(page, size));
        return searchPage.stream().map(SearchHit::getContent).toList();
    }

    public Long count() {
        return productESRepository.count();
    }

    public List<Product> findByCategory(String category, int pageNumber, int pageSize) {
        SearchPage<Product> searchPage = productESRepository.findPageByCategory(category,
                PageRequest.of(pageNumber, pageSize).withSort(Sort.by("category").descending()));

        return searchPage.stream()
                .map(SearchHit::getContent)
                .toList();
    }

    public List<Product> findPageByName(String name, Pageable pageable) {
        Criteria containsName = Criteria.where("name").contains(name);
        CriteriaQuery nameSearchCriteriaQuery = CriteriaQuery.builder(containsName).build();

        SearchHits<Product> searchResults = elasticsearchOperations.search(nameSearchCriteriaQuery, Product.class);

        return searchResults.stream()
                .map(SearchHit::getContent)
                .skip((long) (pageable.getPageNumber() + 1) * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .toList();
    }

    public List<Product> productSearchQuery(String brandName, String categoryName, Double priceRangeFrom, Double priceRangeTo) {

        Query brandquery = Query.of(builder ->
                builder.term(TermQuery.of(t -> t.field("brand").value(brandName))));

        Query categoryQuery = Query.of(builder -> builder.term(TermQuery.of(t -> t.field("category").value(categoryName))));

        Query priceQuery = Query.of(builder -> builder.range(RangeQuery.of(r -> r.number(NumberRangeQuery.of(nrq -> nrq.field("price").lte(priceRangeTo).gte(priceRangeFrom))))));

        Query searchCriteria = Query.of(builder -> builder.bool(BoolQuery.of(bq -> bq.filter(categoryQuery, priceQuery).should(brandquery))));

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(searchCriteria)
                .build();

        return this.elasticsearchOperations.search(nativeQuery, Product.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    public @NotNull Map<String, Aggregate> productAggregations() {

        Aggregation priceAggregation = Aggregation.of(builder -> builder.stats(StatsAggregation.of(sa -> sa.field("price"))));
        Aggregation brandAggregation = Aggregation.of(builder -> builder.terms(TermsAggregation.of(ta -> ta.field("brand"))));

        List<AggregationRange> priceRangeAggregations = List.of(
                AggregationRange.of(builder -> builder.to(150.0)),
                AggregationRange.of(builder -> builder.from(150.0).to(350.0)),
                AggregationRange.of(builder -> builder.from(350.0).to(500.0)),
                AggregationRange.of(builder -> builder.from(500.0).to(750.0)),
                AggregationRange.of(builder -> builder.from(750.0).to(1000.0))
        );

        Aggregation aggregationByPriceRange = Aggregation.of(builder -> builder.range(RangeAggregation.of(pra -> pra.field("price").ranges(priceRangeAggregations))));

        NativeQuery nativeQuery = NativeQuery.builder()
                .withMaxResults(0)
                .withAggregation("price-stats", priceAggregation)
                .withAggregation("group-by-brand-terms", brandAggregation)
                .withAggregation("group-by-price-range", aggregationByPriceRange)
                .build();

        SearchHits<Product> productSearchHits = this.elasticsearchOperations.search(nativeQuery, Product.class);

        List<ElasticsearchAggregation> aggregations = (List<ElasticsearchAggregation>) productSearchHits.getAggregations().aggregations();

        return aggregations.stream()
                .map(ElasticsearchAggregation::aggregation)
                .collect(Collectors.toMap(
                        org.springframework.data.elasticsearch.client.elc.Aggregation::getName,
                        org.springframework.data.elasticsearch.client.elc.Aggregation::getAggregate
                ));
    }

    public Set<String> suggestionsAsYouTypeName(String typedName) {

        FieldSuggester nameSuggester = FieldSuggester.of(builder -> builder.prefix(typedName)
                .completion(CompletionSuggester.of(csb -> csb.field("suggest")
                        .skipDuplicates(true)
                        .size(10))));

        Suggester suggester = Suggester.of(builder -> builder.suggesters("name-suggester", nameSuggester));

        NativeQuery searchQuery = NativeQuery.builder()
                .withSuggester(suggester)
                .withMaxResults(10)
                .withSourceFilter(FetchSourceFilter.of(builder -> builder.withExcludes("*")))
                .build();

        SearchHits<Product> productSearchHits = this.elasticsearchOperations.search(searchQuery, Product.class);

        return Objects.requireNonNull(productSearchHits.getSuggest(), "Suggests can't be null")
                .getSuggestion("name-suggester")
                .getEntries()
                .getFirst()
                .getOptions()
                .stream()
                .map(Suggest.Suggestion.Entry.Option::getText)
                .collect(Collectors.toSet());
    }

}
