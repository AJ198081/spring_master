package dev.aj.elasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.elasticsearch.domain.Product;
import dev.aj.elasticsearch.repositories.ProductESRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
