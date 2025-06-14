package dev.aj.full_stack_v5.order.services.impl;

import dev.aj.full_stack_v5.order.domain.dtos.CartDto;
import dev.aj.full_stack_v5.order.services.CartItemService;
import dev.aj.full_stack_v5.product.repositories.CartItemRepository;
import dev.aj.full_stack_v5.product.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartDto addItemToCart(Long cartId, Long productId, int quantity) {
        return null;
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {

    }

    @Override
    public CartDto getCartItems(Long cartId) {
        return null;
    }

    @Override
    public CartDto updateItemsInCart(Long cartId, Long productId, int quantity) {
        return null;
    }
}
