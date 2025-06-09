package dev.aj.full_stack_v5.product.controllers;

import dev.aj.full_stack_v5.product.domain.dtos.ProductDto;
import dev.aj.full_stack_v5.product.domain.entities.Product;
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
    public ResponseEntity<Product> addProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(productDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProductsWithImagesMeta());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductDto productDto, @PathVariable Long id) {
        return ResponseEntity.ok(productService.updateProduct(productDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategoryName(@PathVariable String categoryName) {
        Iterable<Product> productsIterable = productService.getProductsByCategoryName(categoryName);
        List<Product> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/brand/{brandName}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable String brandName) {
        Iterable<Product> productsIterable = productService.getProductsByBrand(brandName);
        List<Product> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/name/{productName}")
    public ResponseEntity<List<Product>> getProductsByName(@PathVariable String productName) {
        Iterable<Product> productsIterable = productService.getProductsByName(productName);
        List<Product> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryName}/brand/{brandName}")
    public ResponseEntity<List<Product>> getProductsByCategoryNameAndBrand(
            @PathVariable String categoryName,
            @PathVariable String brandName) {
        Iterable<Product> productsIterable = productService.getProductsByCategoryNameAndBrand(categoryName, brandName);
        List<Product> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryName}/name/{productName}")
    public ResponseEntity<List<Product>> getProductsByCategoryNameAndProductName(
            @PathVariable String categoryName,
            @PathVariable String productName
    ) {
        Iterable<Product> productsIterable = productService.getProductsByCategoryNameAndProductName(categoryName, productName);
        List<Product> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/brand/{brandName}/product/{productName}")
    public ResponseEntity<List<Product>> getProductsByBrandAndName(
            @PathVariable String brandName,
            @PathVariable String productName) {
        Iterable<Product> productsIterable = productService.getProductsByBrandAndName(brandName, productName);
        List<Product> products = StreamSupport.stream(productsIterable.spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
}
