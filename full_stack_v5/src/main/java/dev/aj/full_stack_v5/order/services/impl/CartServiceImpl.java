package dev.aj.full_stack_v5.order.services.impl;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import dev.aj.full_stack_v5.order.repositories.CartRepository;
import dev.aj.full_stack_v5.order.services.CartService;
import dev.aj.full_stack_v5.order.services.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    private final CustomerService customerService;

    @Override
    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    public Cart getCartByIdOrThrow(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart with id: %s not found".formatted(id)));
    }

    @Override
    public Cart deleteCartItem(Long cartId, Long cartItemId) {
        return cartRepository.findById(cartId)
                .map(cart -> {
                    cart.removeItem(cart.getCartItems().stream()
                            .filter(cartItem -> cartItem.getId().equals(cartItemId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Cart item with id: %s not found".formatted(cartItemId)))
                    );

                    cartRepository.save(cart);
                    return cart;
                })
                .orElseThrow(() -> new IllegalArgumentException("Cart with id: %s not found".formatted(cartId)));
    }

    @Override
    public Optional<Cart> getCartByCustomerId(Long id) {
        return cartRepository.findCartByCustomerId(id);
    }

    @Override
    public Cart getCartByCustomerIdOrElseNewCart(Long customerId) {
        return cartRepository.findCartByCustomerId(customerId)
                .orElseGet(() -> createCart(customerService.getCustomerById(customerId)));
    }

    @Override
    public Cart createCart(Customer customer) {
        return cartRepository.save(
                Cart.builder()
                        .customer(customer)
                        .build()
        );
    }

    @Override
    public void updateCart(Long id, Cart cart) {

        cartRepository.findById(id)
                .ifPresentOrElse(existingCart -> {
                            existingCart.setCustomer(cart.getCustomer());
                            existingCart.updateCartItems(cart.getCartItems());
                            cartRepository.save(existingCart);
                        },
                        () -> log.error("Unable to find Cart by id {}", id)
                );
    }

    @Override
    public void deleteCart(Long id) {
        cartRepository.findById(id)
                .ifPresentOrElse(
                        cart -> {
                            cart.removeCart();
                            cartRepository.delete(cart);
                            log.info("Cart with id: {} deleted", id);
                        },
                        () -> log.error("Unable to find Cart by id {}, hence wasn't deleted", id)
                );
    }

    public BigDecimal getTotalCartPriceByCartId(Long cartId) {
        return cartRepository.findById(cartId)
                .map(Cart::getTotal)
                .orElseThrow(() -> new IllegalArgumentException("Cart with id: %s not found".formatted(cartId)));
    }

    public BigDecimal getTotalCartPriceByCustomerId(Long customerId) {
        return cartRepository.findCartByCustomerId(customerId)
                .map(Cart::getTotal)
                .orElseThrow(() -> new IllegalArgumentException("Cart with customer id: %s not found".formatted(customerId)));
    }
}
