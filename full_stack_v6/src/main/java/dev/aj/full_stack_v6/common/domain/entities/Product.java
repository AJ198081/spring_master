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
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
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
    private BigDecimal price;

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
    private Set<Image> images = new HashSet<>();

    @Version
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Integer version;

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

}
