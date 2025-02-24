package dev.aj.spring_modulith.order.entities.converters;

import dev.aj.spring_modulith.order.entities.types.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    public static final String STATUS_SUFFIX = " - Status";

    @Override
    public String convertToDatabaseColumn(Status attribute) {
        return attribute.toString().concat(STATUS_SUFFIX);
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return Status.fromCode(dbData.substring(0, dbData.indexOf(STATUS_SUFFIX)));
//        return Status.valueOf(dbData.substring(0, dbData.indexOf(STATUS_SUFFIX)));
    }
}
