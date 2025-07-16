package dev.aj.full_stack_v5.order.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.aj.full_stack_v5.common.domain.AuditMetaData;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_items_gen")
    @SequenceGenerator(name = "order_items_gen", sequenceName = "order_items_seq", initialValue = 100, allocationSize = 10)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int quantity;

    @Column(precision = 10, scale = 2, columnDefinition = "numeric(10,2)")
    private BigDecimal price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Product product;

    @Transient
    private String productName;

    @Builder.Default
    @JsonIgnore
    private AuditMetaData auditMetaData = new AuditMetaData();

    public BigDecimal getOrderItemTotal() {
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }

    public String getProductName() {
        return this.product.getName();
    }
}
