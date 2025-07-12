package dev.aj.full_stack_v5.product.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * DTO for {@link dev.aj.full_stack_v5.product.domain.entities.Category}
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CategoryDto implements Serializable {

    private Long id;

    @NotBlank(message = "Category name can't be null")
    private String name;
}