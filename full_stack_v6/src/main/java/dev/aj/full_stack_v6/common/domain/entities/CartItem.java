package dev.aj.full_stack_v6.common.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
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
@Audited
@AuditTable(value = "cart_item_history")
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
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Product product;

    private Integer quantity;

    private BigDecimal price;

    @Builder.Default
    private double discount = 0.0;

    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

    @SuppressWarnings("unused")
    public BigDecimal getDiscountedPrice() {
        return product.getPrice().multiply(new BigDecimal(quantity));
    }

    public void addProduct(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        product.getCartItems().add(this);
    }

    public void removeCartItem() {
        product.getCartItems().remove(this);
        product = null;
        quantity = null;
        price = null;
    }

    public void updatePrice() {
        this.price = product.getPrice().multiply(new BigDecimal(quantity));
    }
}
