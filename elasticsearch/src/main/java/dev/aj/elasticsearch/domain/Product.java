package dev.aj.elasticsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionContext;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.math.BigDecimal;

@Document(indexName = "product", storeIdInSource = true, createIndex = true)
@Setting(shards = 2, replicas = 2)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "name") // String are analysed
    private String name;

    @CompletionField(maxInputLength = 100, contexts = {
            @CompletionContext(name = "name", type = CompletionContext.ContextMappingType.CATEGORY)
    })
    private Completion suggest;

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Keyword) //Exact word, no analysis
    private String category;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(index = false, type = FieldType.Integer)
    private Integer quantity;

}
