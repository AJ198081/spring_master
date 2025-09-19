package dev.aj.full_stack_v6.category.services.impl;

import dev.aj.full_stack_v6.category.CategoryService;
import dev.aj.full_stack_v6.category.repositories.CategoryRepository;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category saveCategory(Category category) {

        assertCategoryNameUniqueness(category);

        categoryRepository.save(category);
        return category;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity ID %d not found".formatted(id)));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Category '%s' not found".formatted(name)));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(
                        categoryRepository::delete,
                        () -> log.warn("Entity ID {} was not found, hence no deletion occurred", id)
                );

    }

    @Override
    public void patchCategory(Long id, Category modifiedCategory) {

        assertCategoryNameUniqueness(modifiedCategory);

        categoryRepository.findById(id)
                .ifPresentOrElse(existingCategory -> updateCategoryNameIdempotent(modifiedCategory, existingCategory),
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no patch occurred".formatted(modifiedCategory.getId()));
                        });
    }

    private void updateCategoryNameIdempotent(Category modifiedCategory, Category category) {
        if (!category.getName().equals(modifiedCategory.getName())) {
            category.setName(modifiedCategory.getName());
            categoryRepository.save(category);
        } else {
            log.info("No changes were made, as existing entity has same characteristics as the modified category");
        }
    }

    @Override
    public void putCategory(Long id, Category updatedCategory) {

        assertCategoryNameUniqueness(updatedCategory);

        categoryRepository.findById(id)
                .ifPresentOrElse(
                        existingCategory -> updateCategoryNameIdempotent(updatedCategory, existingCategory),
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no put operation occurred".formatted(updatedCategory.getId()));
                        });
    }

    @Override
    public Page<Category> findCategoryPage(String name, int pageNumber, int pageSize, String sortDirection) {

        return categoryRepository.findPageByNameContainingIgnoreCase(
                name,
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), "name"))
        );

    }

    private void assertCategoryNameUniqueness(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new EntityExistsException("Category with name %s already exists".formatted(category.getName()));
        }
    }
}
