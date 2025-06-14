package dev.aj.full_stack_v5.order.domain.entities;

import dev.aj.full_stack_v5.product.domain.entities.Product;
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

@Entity
@Table(name = "cart_items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_gen")
    @SequenceGenerator(name = "cart_item_gen", sequenceName = "cart_item_seq")
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int quantity;

    @Column(precision = 10, scale = 2, columnDefinition = "numeric(10,2)")
    private BigDecimal unitPrice;

    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @OneToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    public void setTotal() {
        this.total = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public BigDecimal getTotal() {
           return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

 }
