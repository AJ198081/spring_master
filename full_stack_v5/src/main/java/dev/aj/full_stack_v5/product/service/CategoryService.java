package dev.aj.full_stack_v5.product.service;

import dev.aj.full_stack_v5.product.domain.dtos.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(CategoryDto categoryDto);
    CategoryDto getCategoryById(Long id);
    CategoryDto getCategoryByName(String categoryName);
    CategoryDto updateCategory(CategoryDto categoryDto, Long id);
    void deleteCategoryById(Long id);
    void deleteCategoryByName(String categoryName);
    List<CategoryDto> getAllCategories();

    List<String> getAvailableCategories();

    CategoryDto addCategory(String categoryName);
}
