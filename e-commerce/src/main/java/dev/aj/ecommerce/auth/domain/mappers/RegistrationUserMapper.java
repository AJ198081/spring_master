package dev.aj.ecommerce.auth.domain.mappers;

import dev.aj.ecommerce.auth.config.AuthConfig;
import dev.aj.ecommerce.auth.domain.dtos.UserRegistrationDto;
import dev.aj.ecommerce.auth.domain.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = AuthConfig.class)
public interface RegistrationUserMapper {

    @Mapping(target = "password", qualifiedByName = "passwordEncoder")
    @Mapping(target = "role", expression = "java(Role.valueOf(\"ROLE_\".concat(userRegistrationDto.getRole().toUpperCase())))")
    User toEntity(UserRegistrationDto userRegistrationDto);



    @Mapping(target = "password", constant = "xxxxxxxxx")
    UserRegistrationDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserRegistrationDto userRegistrationDto, @MappingTarget User user);
}