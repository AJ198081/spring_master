package dev.aj.full_stack_v5.product.repositories;

import dev.aj.full_stack_v5.product.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Iterable<Product> findByCategoryNameAndBrand(String categoryName, String brand);

    Iterable<Product> findByBrandAndName(String brand, String name);

    Iterable<Product> findByBrand(String brand);

    Iterable<Product> findProductByCategoryName(String categoryName);

    @Query(value = "select p from Product p where lower(p.name) ilike lower(concat('%', :name, '%'))")
    Iterable<Product> findProductByName(String name);

    Iterable<Product> findProductByCategoryNameAndName(String categoryName, String productName);

    Optional<Product> findProductByNameAndBrand(String name, String brand);
}
