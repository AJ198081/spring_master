package dev.aj.full_stack_v5.product.service.impl;

import dev.aj.full_stack_v5.product.domain.dtos.CategoryDto;
import dev.aj.full_stack_v5.product.domain.entities.Category;
import dev.aj.full_stack_v5.product.domain.mappers.CategoryMapper;
import dev.aj.full_stack_v5.product.repositories.CategoryRepository;
import dev.aj.full_stack_v5.product.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category persistedCategory = categoryRepository.findCategoryByName(categoryDto.getName())
                .orElseGet(() -> categoryRepository.save(categoryMapper.toCategory(categoryDto)));

        return categoryMapper.toCategoryDto(persistedCategory);
    }

    @Override
    public CategoryDto addCategory(String categoryName) {
        return categoryMapper.toCategoryDto(
                categoryRepository.findCategoryByNameIgnoreCase(categoryName)
                        .orElseGet(() -> categoryRepository.save(
                                Category.builder()
                                        .name(categoryName)
                                        .build())
                        )
        );
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return categoryMapper.toCategoryDto(
                categoryRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Category isn't found with id: " + id))
        );
    }

    @Override
    public CategoryDto getCategoryByName(String categoryName) {
        return categoryMapper.toCategoryDto(
                categoryRepository.findCategoryByName(categoryName)
                        .orElseThrow(() -> new NoSuchElementException("Category isn't found with name: " + categoryName))
        );
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long id) {

        return categoryMapper.toCategoryDto(
                categoryRepository.findCategoryById(id)
                        .map(category -> {
                            category.getProducts().clear();
                            category.setName(categoryDto.getName());
                            return categoryRepository.save(category);
                        })
                        .orElseThrow(() -> new EntityNotFoundException("Category with id: %s not found. Unable to update products.".formatted(id)))
        );

    }

    @Override
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("Category not found with id: " + id);
        }

        categoryRepository.findById(id)
                .ifPresent(category -> {
                            category.getProducts()
                                    .forEach(product -> product.setCategory(null));
                            categoryRepository.delete(category);
                        }
                );
    }

    @Override
    public void deleteCategoryByName(String categoryName) {

        Optional.ofNullable(categoryName)
                .flatMap(categoryRepository::findCategoryByName)
                .ifPresentOrElse(
                        categoryRepository::delete,
                        () -> log.warn("Category with name: {} not found. Unable to delete category.", categoryName)
                );
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryMapper.toCategoryDtos(categoryRepository.findAll());
    }

    @Override
    public List<String> getAvailableCategories() {

        return categoryRepository.findDistinctCategoryNames();
    }
}
