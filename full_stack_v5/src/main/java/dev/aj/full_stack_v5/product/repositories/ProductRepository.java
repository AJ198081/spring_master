package dev.aj.full_stack_v5.product.repositories;

import dev.aj.full_stack_v5.product.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryNameAndBrandName(String categoryName, String brandName);

    List<Product> findByBrandNameAndName(String brand, String name);

    List<Product> findByBrandName(String brand);

    List<Product> findProductByCategoryName(String categoryName);

    @Query(value = "select p from Product p where lower(p.name) ilike lower(concat('%', :name, '%'))")
    List<Product> findProductsByName(String name);

    List<Product> findProductByCategoryNameAndName(String categoryName, String productName);

    Optional<Product> findProductByNameAndBrandName(String name, String brand);

    List<Product> findDistinctByName(String name);

    @Query("select distinct p.brand.name from Product p")
    List<String> getDistinctByBrand();
}
