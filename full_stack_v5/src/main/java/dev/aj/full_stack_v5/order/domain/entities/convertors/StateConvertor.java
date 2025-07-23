package dev.aj.full_stack_v5.order.domain.entities.convertors;

import dev.aj.full_stack_v5.order.domain.entities.enums.STATE;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter
public class StateConvertor implements AttributeConverter<STATE, String> {

    @Override
    public String convertToDatabaseColumn(STATE state) {
        return state.name();
    }

    @Override
    public STATE convertToEntityAttribute(String dbData) {
        return dbData == null
                ? null
                : STATE.valueOf(dbData);
    }
}
