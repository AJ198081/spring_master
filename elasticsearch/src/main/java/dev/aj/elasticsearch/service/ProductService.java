package dev.aj.elasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.elasticsearch.domain.Product;
import dev.aj.elasticsearch.repositories.ProductESRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductESRepository productESRepository;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;


    public List<Product> saveAll(List<Product> products) {
        return Streamable.of(productESRepository.saveAll(products)).toList();
    }

    public List<Product> findByCategory(String category) {
        SearchHits<Product> foundProducts = productESRepository.findByCategory(category);

        return foundProducts.map(SearchHit -> objectMapper.convertValue(SearchHit.getContent(), Product.class))
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

    public Long count() {
        return productESRepository.count();
    }
}
