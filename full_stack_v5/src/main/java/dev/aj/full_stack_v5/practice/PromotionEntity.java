package dev.aj.full_stack_v5.practice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class PromotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promotion_entity_gen")
    @SequenceGenerator(name = "promotion_entity_gen", sequenceName = "promotion_entity_seq")
    @Column(name = "Id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    private String promotionCode;
    private Boolean active;
}
