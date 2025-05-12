package dev.aj.kafka.product.domain.mappers;

import dev.aj.kafka.product.domain.dto.ProductCreateDto;
import dev.aj.kafka.product.domain.dto.ProductCreatedDto;
import dev.aj.kafka.product.domain.entities.Product;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public abstract class ProductMapper {

    public abstract ProductCreatedDto toDto(Product product);

    public abstract Product toEntity(ProductCreateDto dto);

}
