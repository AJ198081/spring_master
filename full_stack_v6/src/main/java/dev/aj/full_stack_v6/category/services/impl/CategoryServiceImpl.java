package dev.aj.full_stack_v6.category.services.impl;

import dev.aj.full_stack_v6.category.CategoryService;
import dev.aj.full_stack_v6.category.repositories.CategoryRepository;
import dev.aj.full_stack_v6.common.domain.entities.Category;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        assertNameUniqueness(category);

        categoryRepository.save(category);
        return category;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity ID %d not found".formatted(id)));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(
                        categoryRepository::delete,
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no deletion occurred".formatted(id));
                        }
                );

    }

    @Override
    public void patchCategory(Long id, Category modifiedCategory) {

        assertNameUniqueness(modifiedCategory);

        categoryRepository.findById(id)
                .ifPresentOrElse(category -> {
                            if (!category.getName().equals(modifiedCategory.getName())) {
                                category.setName(modifiedCategory.getName());
                                categoryRepository.save(category);
                            } else {
                                log.info("No changes were made, as existing entity has same characteristics as the modified category");
                            }
                        },
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no patch occurred".formatted(modifiedCategory.getId()));
                        });
    }

    @Override
    public void putCategory(Long id, Category updatedCategory) {

        assertNameUniqueness(updatedCategory);

        categoryRepository.findById(id)
                .ifPresentOrElse(category -> {
                            category.setName(updatedCategory.getName());
                            categoryRepository.save(category);
                        },
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no put operation occurred".formatted(updatedCategory.getId()));
                        });
    }

    private void assertNameUniqueness(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name %s already exists".formatted(category.getName()));
        }
    }
}
