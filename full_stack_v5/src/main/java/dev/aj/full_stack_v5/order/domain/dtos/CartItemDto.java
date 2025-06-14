package dev.aj.full_stack_v5.order.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal total = BigDecimal.ZERO;
    private String productName;
}
