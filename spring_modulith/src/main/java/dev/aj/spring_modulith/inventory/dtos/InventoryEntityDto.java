package dev.aj.spring_modulith.inventory.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntityDto {
    private UUID inventoryId;
    private String name;
    private String description;
    private int quantity;
    private BigDecimal price;
}
