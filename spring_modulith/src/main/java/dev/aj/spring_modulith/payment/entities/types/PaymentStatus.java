package dev.aj.spring_modulith.payment.entities.types;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("P"),
    SUCCESS("S"),
    FAILED("F");

    private final String code;

    PaymentStatus(String code) {
        this.code = code;
    }
}
