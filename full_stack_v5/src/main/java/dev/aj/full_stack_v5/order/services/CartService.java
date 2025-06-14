package dev.aj.full_stack_v5.order.services;

import dev.aj.full_stack_v5.order.domain.dtos.CartDto;
import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.Customer;

public interface CartService {

    Cart getCart(Long id);

    Cart getCartByCustomerId(Long id);

    CartDto createCart(Customer customer);

    CartDto updateCart(Long id, Cart cart);

    void deleteCart(Long id);

}
