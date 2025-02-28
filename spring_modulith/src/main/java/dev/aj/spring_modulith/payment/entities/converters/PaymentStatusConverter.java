package dev.aj.spring_modulith.payment.entities.converters;

import dev.aj.spring_modulith.payment.entities.types.PaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaymentStatus attribute) {
        return attribute.toString();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String dbData) {
        return Arrays.stream(PaymentStatus.values())
                .filter(paymentStatus -> paymentStatus.getCode().equalsIgnoreCase(dbData))
                .findAny().orElse(PaymentStatus.FAILED);
    }
}
