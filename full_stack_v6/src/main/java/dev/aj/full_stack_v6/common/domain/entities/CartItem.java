package dev.aj.full_stack_v6.common.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(value = {AuditingEntityListener.class})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_generator")
    @SequenceGenerator(name = "cart_item_generator", sequenceName = "cart_item_sequence")
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private Integer quantity;

    private BigDecimal price;

    @Builder.Default
    private double discount = 0.0;

    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

    public BigDecimal getDiscountedPrice() {
        return product.getPrice().multiply(new BigDecimal(quantity));
    }

    public void addProduct(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        product.getCartItems().add(this);
    }

    public void removeProduct() {
        product.getCartItems().remove(this);
    }

    public void updatePrice() {
        this.price = product.getPrice().multiply(new BigDecimal(quantity));
    }
}
