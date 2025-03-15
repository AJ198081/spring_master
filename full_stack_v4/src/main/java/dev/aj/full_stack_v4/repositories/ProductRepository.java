package dev.aj.full_stack_v4.repositories;

import dev.aj.full_stack_v4.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
