package dev.aj.full_stack_v5.product.repositories;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
