package dev.aj.full_stack_v5.product.repositories;

import dev.aj.full_stack_v5.product.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByName(String categoryName);

    Optional<Category> findCategoryById(Long id);

    @Query("select distinct c.name from Category c")
    List<String> findDistinctCategoryNames();

    Optional<Category> findCategoryByNameIgnoreCase(String name);
}
