package dev.aj.elasticsearch.repositories;

import dev.aj.elasticsearch.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductESRepository extends ElasticsearchRepository<Product, String> {

    SearchHits<Product> findByCategory(String category);

    SearchHits<Product> findByCategoryIn(List<String> categories);

    SearchHits<Product> findByBrand(String brandName);

    SearchHits<Product> findDistinctTopByBrand(String brandName);

    SearchPage<Product> findByBrand(String brand, Pageable page);

    @Highlight(fields = {
            @HighlightField(name = "category"),
            @HighlightField(name = "name"),
    }, parameters = @HighlightParameters(preTags = "<b>", postTags = "</b>"))
    SearchPage<Product> findPageByCategory(String category, Pageable pageable);


    SearchHits<Product> findByName(String name);

}
