package dev.aj.full_stack_v5.order.services;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.Customer;

import java.math.BigDecimal;
import java.util.Optional;

public interface CartService {

    Optional<Cart> getCartById(Long id);

    Optional<Cart> getCartByCustomerId(Long id);

    Cart getCartByCustomerIdOrElseNewCart(Long customerId);

    Cart createCart(Customer customer);

    void updateCart(Long id, Cart cart);

    void deleteCart(Long id);

    BigDecimal getTotalCartPriceByCartId(Long cartId);

    BigDecimal getTotalCartPriceByCustomerId(Long cust0omerId);

    Cart getCartByIdOrThrow(Long id);

    Cart deleteCartItem(Long cartId, Long cartItemId);
}
