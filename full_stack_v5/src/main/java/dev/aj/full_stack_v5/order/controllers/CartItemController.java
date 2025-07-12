package dev.aj.full_stack_v5.order.controllers;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.services.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cartItems")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("/")
    public ResponseEntity<Cart> addCartItemToCart(@RequestParam Long customerId, @RequestParam Long productId,  @RequestParam int quantity) {
        return ResponseEntity.ok(cartItemService.addItemToCustomersCart(customerId, productId, quantity));
    }

    @PatchMapping("/")
    public ResponseEntity<Cart> updateCartItemsInTheCart(@RequestParam Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(cartItemService.updateItemsInCart(cartId, productId, quantity));
    }


    @DeleteMapping("/cart/{cartId}/item/{productId}")
    public ResponseEntity<HttpStatus> removeCartItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        cartItemService.removeItemFromCart(cartId, productId);
        return ResponseEntity.ok().build();
    }
}
