package dev.aj.full_stack_v5.order.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carts")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carts_gen")
    @SequenceGenerator(name = "carts_gen", sequenceName = "carts_seq", allocationSize = 25)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(precision = 10, scale = 2, columnDefinition = "numeric(10,2)")
    private BigDecimal total;

    @OneToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<CartItem> cartItems = new HashSet<>();

    public void updateTotal() {
        BigDecimal newTotal = this.cartItems.stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The new value of %f will violate positive cart value invariant".formatted(newTotal));
        }

        this.total = newTotal;
    }

    public void addItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        this.updateTotal();
    }

    public void removeItem(CartItem cartItem) {
        cartItem.setCart(null);
        this.cartItems.remove(cartItem);
        this.updateTotal();
    }

    public void updateCartItems(Set<CartItem> cartItems) {
        this.getCartItems().clear();
        this.getCartItems().addAll(cartItems);
        this.updateTotal();
    }

    public void removeCart() {
        this.getCartItems().forEach(cartItem -> cartItem.setCart(null));
        this.getCartItems().clear();
        this.customer.setCart(null);
    }
}
