package dev.aj.full_stack_v5.product.repositories;

import dev.aj.full_stack_v5.product.domain.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findBrandByName(String brandName);

    @Query("select distinct b.name from Brand b")
    List<String> findAllBrandNames();
}
