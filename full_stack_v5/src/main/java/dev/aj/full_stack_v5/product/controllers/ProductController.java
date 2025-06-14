package dev.aj.full_stack_v5.product.controllers;

import dev.aj.full_stack_v5.product.domain.dtos.ProductRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductResponseDto;
import dev.aj.full_stack_v5.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/")
    public ResponseEntity<ProductResponseDto> addProduct(@RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(productRequestDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProductsWithImagesMeta());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        Optional<ProductResponseDto> product = productService.getProductResponseDtoByProductId(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody ProductRequestDto productDto, @PathVariable Long id) {
        return ResponseEntity.ok(productService.updateProduct(productDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategoryName(@PathVariable String categoryName) {
        Iterable<ProductResponseDto> productsIterable = productService.getProductsByCategoryName(categoryName);
        return ResponseEntity.ok(StreamSupport.stream(productsIterable.spliterator(), false)
                .toList());
    }

    @GetMapping("/brand/{brandName}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByBrand(@PathVariable String brandName) {
        Iterable<ProductResponseDto> productsIterable = productService.getProductsByBrand(brandName);
        List<ProductResponseDto> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/name/{productName}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByName(@PathVariable String productName) {
        Iterable<ProductResponseDto> productsIterable = productService.getProductsByName(productName);
        List<ProductResponseDto> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryName}/brand/{brandName}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategoryNameAndBrand(
            @PathVariable String categoryName,
            @PathVariable String brandName) {
        Iterable<ProductResponseDto> productsIterable = productService.getProductsByCategoryNameAndBrand(categoryName, brandName);
        List<ProductResponseDto> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryName}/name/{productName}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategoryNameAndProductName(
            @PathVariable String categoryName,
            @PathVariable String productName
    ) {
        Iterable<ProductResponseDto> productsIterable = productService.getProductsByCategoryNameAndProductName(categoryName, productName);
        List<ProductResponseDto> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/brand/{brandName}/product/{productName}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByBrandAndName(
            @PathVariable String brandName,
            @PathVariable String productName) {
        Iterable<ProductResponseDto> productsIterable = productService.getProductsByBrandAndName(brandName, productName);
        return ResponseEntity.ok(StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList()));
    }
}
