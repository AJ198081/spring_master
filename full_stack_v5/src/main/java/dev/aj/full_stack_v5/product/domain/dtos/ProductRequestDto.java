package dev.aj.full_stack_v5.product.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductRequestDto {

    @Size(message = "Product name can't be blank", min = 2, max = 100)
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String brand;

    private BigDecimal price;

    private Integer inventory;

    private String categoryName;

    @Builder.Default
    private Set<ImageRequestDto> images = new HashSet<>();

    private Boolean inWishList;
}
