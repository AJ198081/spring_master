package dev.aj.kafka.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductCreatedDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
