package dev.aj.full_stack_v6.common.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Audited
@AuditTable(value = "product_history")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_gen")
    @SequenceGenerator(name = "product_gen", sequenceName = "prod_sequence")
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal discountedPrice;

    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder.Default
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "product",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @NotAudited
    private Set<Image> images = new HashSet<>();

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", referencedColumnName = "id",  nullable = false, updatable = false)
    @JsonIgnore
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Seller seller;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @NotAudited
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.MERGE)
    @JsonIgnore
    @NotAudited
    private List<OrderItem> orderItems;

    @Version
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Integer version;

    @Embedded
    @Builder.Default
    @NotAudited
    private AuditMetaData auditMetaData = new AuditMetaData();
}
