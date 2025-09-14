package dev.aj.full_stack_v6.category;

import dev.aj.full_stack_v6.common.domain.entities.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();
    Category saveCategory(Category category);
    Category getCategoryById(Long id);
    void deleteCategoryById(Long id);

    void patchCategory(Long id, Category modifiedCategory);

    void putCategory(Long id, Category updatedCategory);

}
