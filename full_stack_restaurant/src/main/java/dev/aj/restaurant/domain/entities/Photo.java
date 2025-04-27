package dev.aj.restaurant.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Photo {
    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
