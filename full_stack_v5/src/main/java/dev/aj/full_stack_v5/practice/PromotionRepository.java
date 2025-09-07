package dev.aj.full_stack_v5.practice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

    @Query("SELECT COUNT(p) > 0 FROM PromotionEntity p WHERE p.promotionCode = :code")
    boolean existsByCode(@Param("code") String code);
}