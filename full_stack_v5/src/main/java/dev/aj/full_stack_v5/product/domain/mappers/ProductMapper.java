package dev.aj.full_stack_v5.product.domain.mappers;

import dev.aj.full_stack_v5.product.domain.dtos.ProductRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Image;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jspecify.annotations.NonNull;
import org.mapstruct.AnnotateWith;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN,
        injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
@AnnotateWith(
        value = AllArgsConstructor.class
)
public abstract class ProductMapper {

    private ImageMapper imageMapper;

    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target="brand", source = "product.brand.name")
    public abstract ProductResponseDto toProductResponseDto(Product product);


    @Mapping(target = "images", expression = "java(this.getImagesIfPresent(productRequestDto))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditMetaData", ignore = true)
    @Mapping(target = "category.name", source = "categoryName")
    @Mapping(target = "brand.name", source = "brand")
    public abstract Product toProduct(ProductRequestDto productRequestDto);

    @SneakyThrows
    Set<Image> getImagesIfPresent(ProductRequestDto productRequestDto) {
        if (productRequestDto.getImages() != null && !productRequestDto.getImages().isEmpty()) {
            return productRequestDto.getImages().stream()
                    .map(imageRequestDto -> {
                        try {
                            return imageMapper.toImage(imageRequestDto);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());
        }
        return null;
    }

    public abstract List<ProductResponseDto> toProductResponseDtos(@NonNull List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.name", source = "categoryName")
    @Mapping(target = "images", expression = "java(this.getImagesIfPresent(productRequestDto, existingProduct))")
    @Mapping(target = "brand.name", source = "brand")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditMetaData", ignore = true)
    public abstract Product patchDtoToProduct(ProductRequestDto productRequestDto, @MappingTarget Product existingProduct);

    Set<Image> getImagesIfPresent(ProductRequestDto productRequestDto, Product existingProduct) {
        if (productRequestDto != null && productRequestDto.getImages() != null && !productRequestDto.getImages().isEmpty()) {
            return productRequestDto.getImages().stream()
                    .map(imageRequestDto -> {
                        try {
                            return imageMapper.toImage(imageRequestDto);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());
        } else {
            return existingProduct.getImages();
        }
    }
}
