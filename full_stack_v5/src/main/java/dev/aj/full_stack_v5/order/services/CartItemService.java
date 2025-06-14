package dev.aj.full_stack_v5.order.services;

import dev.aj.full_stack_v5.order.domain.dtos.CartDto;

public interface CartItemService {

    CartDto addItemToCart(Long cartId, Long productId, int quantity);

    void removeItemFromCart(Long cartId, Long productId);

    CartDto getCartItems(Long cartId);

    CartDto updateItemsInCart(Long cartId, Long productId, int quantity);

}
