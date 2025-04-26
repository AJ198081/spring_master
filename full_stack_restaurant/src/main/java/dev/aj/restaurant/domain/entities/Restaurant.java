package dev.aj.restaurant.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "restaurants")
public class Restaurant {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String cuisineType;

    @Field(type = FieldType.Keyword)
    private String contactInformation;

    @Field(type = FieldType.Float)
    private Float averageRating;

    @Field(type = FieldType.Integer)
    private Integer numberOfRatings;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Nested)
    private Address address;

    @Field(type = FieldType.Nested)
    private OperatingHours operatingHours;

    @Builder.Default
    @Field(type = FieldType.Nested)
    private List<Photo> photos = new ArrayList<>();

    @Builder.Default
    @Field(type = FieldType.Nested)
    private List<Review> reviews = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private User createdBy;
}
