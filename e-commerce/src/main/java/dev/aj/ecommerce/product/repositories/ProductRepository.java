package dev.aj.ecommerce.product.repositories;

import dev.aj.ecommerce.product.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsFeaturedTrue();

    List<Product> findByNameContainingIgnoreCase(String name);
}
