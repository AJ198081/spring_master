package dev.aj.full_stack_v6.category.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    Page<Category> findPageByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    Optional<Category> findByName(String name);

}
