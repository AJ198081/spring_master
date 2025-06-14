package dev.aj.full_stack_v5.order.repositories;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findCartById(Long id);

    Optional<Cart> findCartByCustomerId(Long customerId);
}
