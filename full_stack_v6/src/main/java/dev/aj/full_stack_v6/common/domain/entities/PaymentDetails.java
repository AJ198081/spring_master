package dev.aj.full_stack_v6.common.domain.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PaymentDetails {
    private String cardHolderName;
    private String cardType;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
}
