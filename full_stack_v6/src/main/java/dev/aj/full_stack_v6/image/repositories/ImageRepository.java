package dev.aj.full_stack_v6.image.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByProduct_Id(Long productId);

    void deleteByProduct_Id(Long productId);

    @Query("SELECT i FROM Image i WHERE i.product.name = :productName")
    List<Image> findByProduct_Name(String productName);

    Set<Image> findImageByFileName(String fileName);
}
