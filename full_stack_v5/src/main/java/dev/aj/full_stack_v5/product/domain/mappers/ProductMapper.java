package dev.aj.full_stack_v5.product.domain.mappers;

import dev.aj.full_stack_v5.product.domain.dtos.ProductDto;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN
)
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toProductDto(Product product);


    @Mapping(target = "images", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditMetaData", ignore = true)
    @Mapping(target = "category.name", source = "categoryName")
    Product toProduct(ProductDto productDto);
}
