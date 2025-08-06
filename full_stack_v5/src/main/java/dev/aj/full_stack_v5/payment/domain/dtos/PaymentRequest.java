package dev.aj.full_stack_v5.payment.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private String stripeToken;
    private Long customerId;
    private BigDecimal amount;

    @Builder.Default
    private String currency = "AUD";

    public void setAmount(BigDecimal actualAmount) {
        if (!Objects.isNull(actualAmount)) {
            this.amount = actualAmount.multiply(BigDecimal.valueOf(100));
        }
    }
}
