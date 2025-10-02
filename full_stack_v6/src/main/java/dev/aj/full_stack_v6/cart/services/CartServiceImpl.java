package dev.aj.full_stack_v6.cart.services;

import dev.aj.full_stack_v6.cart.CartItemService;
import dev.aj.full_stack_v6.cart.CartService;
import dev.aj.full_stack_v6.cart.repositories.CartRepository;
import dev.aj.full_stack_v6.common.domain.entities.Cart;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.common.domain.entities.User;
import dev.aj.full_stack_v6.product.ProductService;
import dev.aj.full_stack_v6.security.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

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

        Product product;

        try {
            product = productService.getProductById(productId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Product id: %d is no longer available for sale".formatted(productId));
        }

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product Id: %d, only %d left".formatted(productId, product.getStock()));
        }

        cart.addCartItem(product, quantity);

        cart.updateTotalCartPrice();

        cartRepository.save(cart);
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

    private Cart getUserCart(Principal principal) {
        User user = userService.loadUser(principal.getName());

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> Cart.builder()
                        .user(user)
                        .build());
    }
}
