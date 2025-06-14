package dev.aj.full_stack_v5.product.domain.entities;

import dev.aj.full_stack_v5.common.domain.AuditMetaData;
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
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_generator")
    @SequenceGenerator(name = "product_seq_generator", sequenceName = "product_sequence", initialValue = 100, allocationSize = 10)
    @Column(name = "id", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;


    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "unit_price", precision = 10, scale = 2, columnDefinition = "numeric(10,2)")
    private BigDecimal price;

    private int inventory;

    private String brand;

    @ToString.Exclude
    @ManyToOne(cascade = {
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH,
            CascadeType.PERSIST
    })
    @JoinColumn(name = "category_id")
    private Category category;

    @ToString.Exclude
    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Image> images;

    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();
}
