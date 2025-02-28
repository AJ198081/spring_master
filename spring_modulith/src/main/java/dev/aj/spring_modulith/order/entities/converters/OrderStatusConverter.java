package dev.aj.spring_modulith.order.entities.converters;

import dev.aj.spring_modulith.order.entities.types.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    public static final String STATUS_SUFFIX = " - Status";

    @Override
    public String convertToDatabaseColumn(OrderStatus attribute) {
        return attribute.toString().concat(STATUS_SUFFIX);
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        return OrderStatus.fromCode(dbData.substring(0, dbData.indexOf(STATUS_SUFFIX)));
//        return Status.valueOf(dbData.substring(0, dbData.indexOf(STATUS_SUFFIX)));
    }
}
