package dev.aj.kafka.product.repositories;

import dev.aj.kafka.product.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository  extends JpaRepository<Product, Long> {
}
