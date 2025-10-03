package dev.aj.full_stack_v6.cart;

import dev.aj.full_stack_v6.common.domain.entities.Cart;

import java.security.Principal;
import java.util.List;

public interface CartService {
    void addProductToCart(Long productId, Integer quantity, Principal principal);

    Cart getCart(Principal principal);

    List<Cart> getAllCarts();

    Cart putQuantityToCart(Long productId, Integer quantity, Principal principal);
}
