package dev.aj.full_stack_v6.product.controllers;

import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${PRODUCT_API_PATH:/api/v1/products}")
@RequiredArgsConstructor
class ProductController {

    private final ProductService productService;

    @PostMapping("/")
    public ResponseEntity<Product> saveProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Product>> getProductsByPage(
            @RequestParam("name") String name,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortDirection") String sortDirection) {
        return ResponseEntity.ok(productService.findProductPage(name, page, size, sortDirection));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> putProduct(@PathVariable("id") Long id, @RequestBody @Validated Product product) {
        productService.putProduct(id, product);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        productService.patchProduct(id, product);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }
}
