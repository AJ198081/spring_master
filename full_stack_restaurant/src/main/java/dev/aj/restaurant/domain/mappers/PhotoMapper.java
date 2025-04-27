package dev.aj.restaurant.domain.mappers;

import dev.aj.restaurant.domain.dtos.PhotoDto;
import dev.aj.restaurant.domain.entities.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PhotoMapper {

    PhotoDto toDto(Photo photo);

    Photo toEntity(PhotoDto photoDto);

}
