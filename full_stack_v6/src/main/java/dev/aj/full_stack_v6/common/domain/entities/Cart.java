package dev.aj.full_stack_v6.common.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(value = {AuditingEntityListener.class})
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_generator")
    @SequenceGenerator(name = "cart_generator", sequenceName = "cart_sequence")
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    private BigDecimal totalPrice;

    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

    public void addCartItem(Product product, Integer quantity) {

        cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                .findFirst()
                .ifPresentOrElse(
                        cartItem -> {
                            cartItem.setQuantity(cartItem.getQuantity() + quantity);
                            cartItem.updatePrice();
                        },
                        () -> {
                            CartItem newCartItem = new CartItem();
                            newCartItem.addProduct(product, quantity);

                            newCartItem.setCart(this);
                            cartItems.add(newCartItem);

                            newCartItem.updatePrice();
                        }
                );

        product.setStock(product.getStock() - quantity);
    }

    public void updateTotalCartPrice() {
        this.totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void removeCartItem(Long productId) {
        Optional<CartItem> cartItemOptional = cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst();

        if (cartItemOptional.isPresent()) {
            CartItem cartItemToBeRemoved = cartItemOptional.get();
            cartItemToBeRemoved.removeProduct();
            this.getCartItems().remove(cartItemToBeRemoved);
            updateTotalCartPrice();
        }
    }
}
