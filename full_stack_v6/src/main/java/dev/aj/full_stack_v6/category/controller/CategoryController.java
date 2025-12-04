package dev.aj.full_stack_v6.category.controller;

import dev.aj.full_stack_v6.category.CategoryService;
import dev.aj.full_stack_v6.common.domain.entities.Category;
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
@RequestMapping("${CATEGORY_API_PATH}")
@RequiredArgsConstructor
class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/")
    public ResponseEntity<Category> saveCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.saveCategory(category));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Category>> getCategoriesByPage(
            @RequestParam("name") String name,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortDirection") String sortDirection) {
        return ResponseEntity.ok(categoryService.findCategoryPage(name, page, size, sortDirection));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category (Put operation)", responses = {
            @ApiResponse(description = "Category updated successfully", responseCode = "202")
    })
    public ResponseEntity<Void> putCategory(@PathVariable(value = "id") Long id, @RequestBody @Validated Category category) {
        categoryService.putCategory(id, category);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> patchCategory(@PathVariable(value = "id") Long id, @RequestBody Category category) {
        categoryService.patchCategory(id, category);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable(value = "id") Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }
}
