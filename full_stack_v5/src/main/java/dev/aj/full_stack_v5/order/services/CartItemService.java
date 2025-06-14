package dev.aj.full_stack_v5.order.services;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.CartItem;

public interface CartItemService {

    Cart addItemToCart(Long cartId, Long productId, int quantity);

    Cart addItemToCustomersCart(Long customerId, Long productId, int quantity);

    void removeItemFromCart(Long cartId, Long productId);

    CartItem getCartItem(Long cartId, Long productId);

    Cart updateItemsInCart(Long cartId, Long productId, int quantity);

}
