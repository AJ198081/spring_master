package dev.aj.full_stack_v5.order.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.aj.full_stack_v5.order.domain.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Audited
@AuditTable(schema = "public", value = "order_audits")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_gen")
    @SequenceGenerator(name = "orders_gen", sequenceName = "orders_seq", allocationSize = 1)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    private ZonedDateTime orderDate;

    @Builder.Default
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime shipDate = null;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String comments;

    @Builder.Default
    @Column(precision = 10, scale = 2, columnDefinition = "numeric(10,2)")
    private BigDecimal total = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Set<OrderItem> orderItems = new HashSet<>();

    public void updateTotal() {
        this.total = this.orderItems.stream()
                .map(OrderItem::getOrderItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
