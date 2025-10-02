package dev.aj.full_stack_v6.cart.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c where c.user.id = ?1")
    Optional<Cart> findByUserId(Long id);


}