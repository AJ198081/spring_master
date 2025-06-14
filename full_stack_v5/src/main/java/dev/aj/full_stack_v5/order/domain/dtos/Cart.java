package dev.aj.full_stack_v5.order.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart implements Serializable {
    private BigDecimal total;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerPhone;

    @Builder.Default
    private Set<CartItemDto> cartItems = new HashSet<>();
}