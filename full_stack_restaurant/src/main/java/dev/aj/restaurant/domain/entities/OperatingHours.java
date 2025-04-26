package dev.aj.restaurant.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperatingHours {

    @Field(type = FieldType.Nested)
    private TimeRange monday;

    @Field(type = FieldType.Nested)
    private TimeRange tuesday;

    @Field(type = FieldType.Nested)
    private TimeRange wednesday;

    @Field(type = FieldType.Nested)
    private TimeRange thursday;

    @Field(type = FieldType.Nested)
    private TimeRange friday;

    @Field(type = FieldType.Nested)
    private TimeRange saturday;

    @Field(type = FieldType.Nested)
    private TimeRange sunday;
}
