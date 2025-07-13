package dev.aj.full_stack_v5.order.services.impl;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.CartItem;
import dev.aj.full_stack_v5.order.repositories.CartRepository;
import dev.aj.full_stack_v5.order.services.CartItemService;
import dev.aj.full_stack_v5.order.services.CartService;
import dev.aj.full_stack_v5.order.services.CustomerService;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import dev.aj.full_stack_v5.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {

    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;

    @Override
    public Cart addItemToCart(Long cartId, Long productId, int quantity) {

        Optional<Product> optionalProduct = productService.getProductById(productId);
        Optional<Cart> cart = cartService.getCartById(cartId);

        if (optionalProduct.isEmpty() || cart.isEmpty()) {
            log.error("Product with id: {} not found or cart with id: {} not found", productId, cartId);
            throw new IllegalArgumentException("Product with id: %s not found or cart with id: %s not found".formatted(productId, cartId));
        }

        Cart existingCart = cart.get();
        Product existingProduct = optionalProduct.get();

        CartItem cartItemToBePersisted = existingCart.getCartItems()
                .stream()
                .filter(cItem -> Objects.equals(cItem.getProduct().getId(), productId))
                .findFirst()
                .orElseGet(() -> CartItem.builder()
                        .cart(existingCart)
                        .product(existingProduct)
                        .build());

        cartItemToBePersisted.setQuantity(quantity);
        cartItemToBePersisted.setUnitPrice(existingProduct.getPrice());
        cartItemToBePersisted.setTotal(BigDecimal.valueOf(quantity).multiply(existingProduct.getPrice()));

        existingCart.getCartItems().add(cartItemToBePersisted);

        return cartRepository.save(existingCart);
    }

    @Override
    public Cart addItemToCustomersCart(Long customerId, Long productId, int quantity) {

        Optional<Product> optionalProduct = productService.getProductById(productId);
        Optional<Cart> optionalCart = cartService.getCartByCustomerId(customerId);

        if (optionalProduct.isEmpty()) {
            log.error("Product with id: {} not found or cart for customerId: {} not found", productId, customerId);
            throw new IllegalArgumentException("Product with id: %s not found or cart with id: %s not found".formatted(productId, customerId));
        }

        Product existingProduct = optionalProduct.get();
        Cart existingCart = optionalCart.orElseGet(() -> Cart.builder()
                .customer(customerService.getCustomerById(customerId))
                .build());

        @SuppressWarnings("Duplicate")
        CartItem cartItemToBePersisted = existingCart.getCartItems()
                .stream()
                .filter(cItem -> Objects.equals(cItem.getProduct().getId(), productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newCartItem = CartItem.builder()
                            .cart(existingCart)
                            .product(existingProduct)
                            .build();

                    existingCart.getCartItems().add(newCartItem);

                    return newCartItem;
                });

        if (cartItemToBePersisted.getQuantity() == 0) {
            cartItemToBePersisted.setQuantity(quantity);
        } else {
            cartItemToBePersisted.setQuantity(cartItemToBePersisted.getQuantity() + quantity);
        }

        cartItemToBePersisted.setUnitPrice(existingProduct.getPrice());
        cartItemToBePersisted.setTotal(BigDecimal.valueOf(cartItemToBePersisted.getQuantity()).multiply(existingProduct.getPrice()));

        existingCart.updateTotal();

        return cartRepository.save(existingCart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        CartItem cartItem = getCartItem(cartId, productId);

        Cart cart = cartService.getCartById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart with id: %s not found".formatted(cartId)));

        cart.removeItem(cartItem);

        cartService.updateCart(cartId, cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {

        return cartService.getCartById(cartId)
                .map(Cart::getCartItems)
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(cartItems -> Objects.equals(cartItems.getProduct().getId(), productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product with id: %s not found in cart with id: %s".formatted(productId, cartId)));
    }

    @Override
    public Cart updateItemsInCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCartById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart with id: %s not found".formatted(cartId)));

        cart.getCartItems()
                .stream()
                .filter(cartItem -> Objects.equals(cartItem.getProduct().getId(), productId))
                .findFirst()
                .ifPresentOrElse(item -> {
                            item.setQuantity(quantity);
                            item.setTotal(BigDecimal.valueOf(quantity).multiply(item.getProduct().getPrice()));
                        },
                        () -> log.error("Unable to find cart item with id: {} in cart with id: {}", productId, cartId)
                );

        cart.setTotal(cart.getCartItems()
                .stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        cartService.updateCart(cartId, cart);
        return cart;
    }
}
