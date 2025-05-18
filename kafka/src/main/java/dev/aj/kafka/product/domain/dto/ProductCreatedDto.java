package dev.aj.kafka.product.domain.dto;

import dev.aj.kafka.product.domain.entities.embeddable.AuditMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ProductCreatedDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;

    @Builder.Default
    private AuditMetadata auditMetadata = new AuditMetadata();
}
