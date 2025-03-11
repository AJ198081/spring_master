package dev.aj.elasticsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "reviews", storeIdInSource = true)
@Setting(shards = 2, replicas = 2)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    private String id;

    @Field(type = FieldType.Text) //https://www.elastic.co/guide/en/elasticsearch/reference/current/text.html
    private String author;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword) //https://www.elastic.co/guide/en/elasticsearch/reference/current/keyword.html
    private String type;
}
