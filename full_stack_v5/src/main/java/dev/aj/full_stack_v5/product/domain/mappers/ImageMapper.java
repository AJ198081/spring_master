package dev.aj.full_stack_v5.product.domain.mappers;

import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Image;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ImageMapper {

    @Mapping(target = "downloadUrl", ignore = true)
    @Mapping(target = "contents", expression = "java(imageDto.getFile().getBytes())") // This can throw IOException
    @Mapping(target = "contentType", expression = "java(imageDto.getFile().getContentType())")
    @Mapping(target = "fileName", expression = "java(imageDto.getFile().getOriginalFilename())")
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditMetaData", ignore = true)
    Image toImage(ImageRequestDto imageDto) throws IOException;

    @Mapping(target = "downloadUrl", expression = "java(\"/download/\".concat(String.valueOf(image.getId())))")
    ImageResponseDto toImageDto(Image image);


    List<ImageResponseDto> toImageDtos(List<Image> images);

    List<Image> toImages(List<ImageRequestDto> images);

    Set<ImageResponseDto> toImageDtos(Set<Image> images);
}
