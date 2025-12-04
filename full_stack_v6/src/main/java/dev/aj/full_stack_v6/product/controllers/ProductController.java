package dev.aj.full_stack_v6.product.controllers;

import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(summary = "Update a product (Put operation)", responses = {
            @ApiResponse(description = "Product updated successfully", responseCode = "202")
    })
    public ResponseEntity<Void> putProduct(@PathVariable("id") Long id, @RequestBody @Validated Product product) {
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
