package dev.aj.full_stack_v6.cart.controllers;

import dev.aj.full_stack_v6.cart.CartService;
import dev.aj.full_stack_v6.common.domain.entities.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${CART_API_PATH}")
@RequiredArgsConstructor
class CartController {

    private final CartService cartService;

    @PostMapping("/")
    public ResponseEntity<HttpStatus> addProductToCart(@RequestParam Long productId, @RequestParam Integer quantity, Principal principal) {
        cartService.addProductToCart(productId, quantity, principal);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/")
    public ResponseEntity<Cart> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCart(principal));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Cart>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }


}
