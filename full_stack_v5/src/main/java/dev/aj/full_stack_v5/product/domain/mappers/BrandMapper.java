package dev.aj.full_stack_v5.product.domain.mappers;

import dev.aj.full_stack_v5.product.domain.dtos.BrandDto;
import dev.aj.full_stack_v5.product.domain.entities.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN
)
public interface BrandMapper {

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditMetaData", ignore = true)
    Brand toBrand(BrandDto brandDto);

    @Mapping(target = "id", source = "id")
    BrandDto toBrandDto(Brand brand);

    List<BrandDto> toBrandDtos(List<Brand> brands);
}