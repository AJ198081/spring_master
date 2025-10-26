package dev.aj.full_stack_v6.common.domain.dtos;


import dev.aj.full_stack_v6.common.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistory {
    private Long id;
    private UUID orderId;
    private OrderStatus orderStatus;
    private Long customerId;
    private Long paymentId;
    private BigDecimal totalPrice;
    private BigDecimal shippingPrice = BigDecimal.TEN;
}
