package dev.aj.full_stack_v6.category.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
