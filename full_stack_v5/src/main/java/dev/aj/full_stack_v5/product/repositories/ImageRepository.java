package dev.aj.full_stack_v5.product.repositories;

import dev.aj.full_stack_v5.product.domain.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByProductId(Long productId);

    @Query("SELECT i FROM Image i WHERE i.product.name = :productName")
    List<Image> findByProductName(String productName);
}
