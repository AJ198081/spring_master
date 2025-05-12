package dev.aj.kafka.product.controllers;

import dev.aj.kafka.product.domain.dto.ProductCreateDto;
import dev.aj.kafka.product.domain.dto.ProductCreatedDto;
import dev.aj.kafka.product.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductCreatedDto> createProduct(@RequestBody ProductCreateDto productCreateDto) {
        ProductCreatedDto productCreatedDto = productService.createProduct(productCreateDto);

        return ResponseEntity
                .created(URI.create("/%d".formatted(productCreatedDto.getId())))
                .body(productCreatedDto);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllProducts() {
        productService.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }

}
