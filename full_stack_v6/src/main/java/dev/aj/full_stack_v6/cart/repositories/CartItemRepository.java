package dev.aj.full_stack_v6.cart.repositories;

import dev.aj.full_stack_v6.common.domain.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}