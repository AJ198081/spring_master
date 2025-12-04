package dev.aj.full_stack_v6.cart.controllers;

import dev.aj.full_stack_v6.cart.CartService;
import dev.aj.full_stack_v6.common.domain.entities.Cart;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @Operation(summary = "Add product to cart", responses = {
            @ApiResponse(responseCode = "202", description = "Accepted")
    })
    public ResponseEntity<Void> addProductToCart(@RequestParam Long productId,
                                                       @RequestParam @Positive Integer quantity,
                                                       Principal principal) {
        cartService.addProductToCart(productId, quantity, principal);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/")
    public ResponseEntity<Cart> getUserCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCart(principal));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Cart>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @PutMapping("/")
    public ResponseEntity<Cart> patchCart(@RequestParam Long productId,
                                          @RequestParam @Positive Integer quantity,
                                          Principal principal) {
        return ResponseEntity.ok(cartService.putQuantityToCart(productId, quantity, principal));
    }

    @DeleteMapping("/product")
      @Operation(summary = "Add product to cart", responses = {
            @ApiResponse(responseCode = "204", description = "Accepted")
    })
    public ResponseEntity<Void> deleteCart(@RequestParam Long productId, Principal principal) {
        cartService.deleteProduct(productId, principal);
        return ResponseEntity.noContent().build();
    }


}
