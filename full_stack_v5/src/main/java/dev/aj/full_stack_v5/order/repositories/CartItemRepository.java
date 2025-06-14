package dev.aj.full_stack_v5.order.repositories;

import dev.aj.full_stack_v5.order.domain.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findCartItemByProductId(Long id);
}
