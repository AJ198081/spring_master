package dev.aj.full_stack_v6.cart.services;

import dev.aj.full_stack_v6.cart.CartService;
import dev.aj.full_stack_v6.cart.repositories.CartRepository;
import dev.aj.full_stack_v6.common.domain.entities.Cart;
import dev.aj.full_stack_v6.common.domain.entities.CartItem;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.common.domain.entities.User;
import dev.aj.full_stack_v6.common.domain.events.ProductPriceUpdatedEvent;
import dev.aj.full_stack_v6.product.ProductService;
import dev.aj.full_stack_v6.security.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;

    @Override
    public void addProductToCart(Long productId, Integer quantity, Principal principal) {

        Cart cart = getUserCart(principal);
        Product product = getProductOrThrow(productId);

        canAddQuantity(product, quantity);

        cart.addCartItem(product, quantity);

        cart.updateTotalCartPrice();

        cartRepository.save(cart);
    }

    private static void canAddQuantity(Product product, Integer quantityToBeAdded) {
        if (product.getStock() < quantityToBeAdded) {
            throw new IllegalArgumentException("Insufficient stock for product: %s, only %d left".formatted(product.getName(), product.getStock()));
        }
    }

    private Product getProductOrThrow(Long productId) {
        Product product;

        try {
            product = productService.getProductById(productId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Product id: %d is no longer available for sale".formatted(productId));
        }
        return product;
    }

    @Override
    public Cart getCart(Principal principal) {
        return getUserCart(principal);
    }

    @Override
    @PostAuthorize("hasRole('ADMIN')")
    public List<Cart> getAllCarts() {
        List<Cart> allCarts = cartRepository.findAll();
        log.info("Retrieved {} carts", allCarts.size());
        return allCarts;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CUSTOMER')")
    public Cart putQuantityToCart(Long productId, Integer quantity, Principal principal) {
        Cart cart = getUserCart(principal);
        Product product = getProductOrThrow(productId);

        Integer quantityAlreadyInCart = cart.getCartItems()
                .stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                .map(CartItem::getQuantity)
                .findFirst()
                .orElse(0);

        if (Objects.equals(quantity, quantityAlreadyInCart)) {
            return cart;
        }

        canAddQuantity(product, Math.abs(quantity - quantityAlreadyInCart));

        cart.addCartItem(product, quantity);
        cart.updateTotalCartPrice();

        return cartRepository.save(cart);
    }

    @Override
    public void deleteProduct(Long productId, Principal principal) {
        Cart userCart = getUserCart(principal);
        userCart.removeCartItem(productId);
        cartRepository.save(userCart);
    }

    @Override
    @EventListener
    public void updateCartsContainingProduct(ProductPriceUpdatedEvent productUpdatedEvent) {
        cartRepository.findAll()
                .stream()
                .filter(cart -> containsProductId(cart, productUpdatedEvent.id()))
                .map(this::refreshCartPrices)
                .forEach(cartRepository::save);
    }

    private boolean containsProductId(Cart cart, Long productId) {
        return cart.getCartItems()
                .stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    private Cart refreshCartPrices(Cart cart) {
        BigDecimal currentTotalCartPrice = cart.getTotalPrice();
        cart.getCartItems()
                .forEach(CartItem::updatePrice);
        cart.updateTotalCartPrice();
        log.info("Refreshed cart prices from {} to {} for cart id: {}", currentTotalCartPrice, cart.getTotalPrice(), cart.getId());
        return cart;
    }

    private Cart getUserCart(Principal principal) {
        User user = userService.loadUser(principal.getName());

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> Cart.builder()
                        .user(user)
                        .build());
    }
}
