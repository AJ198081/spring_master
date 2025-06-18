package dev.aj.full_stack_v5.auth.domain.mapper;

import dev.aj.full_stack_v5.auth.domain.dtos.SecurityUser;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.auth.domain.entities.Role;
import dev.aj.full_stack_v5.auth.domain.entities.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditMetaData", ignore = true)
    @Mapping(target = "roles", qualifiedByName = "mapSetStringsToSetRoles", source = "userRegistrationDto.roles")
    public abstract User userRegistrationToUser(UserRegistrationDto userRegistrationDto);

    @Mapping(target = "roles", qualifiedByName = "mapSetRolesToSetStrings", source = "user.roles")
    public abstract UserResponseDto userToUserResponseDto(User user);

    @Named(value = "mapSetStringsToSetRoles")
    public Set<Role> mapSetStringsToSetRoles(Set<String> roleNames) {
        return roleNames
                .stream()
                .map(roleName -> Role.builder()
                        .name(roleName)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named(value = "mapSetRolesToSetStrings")
    public Set<String> mapSetRolesToSetStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
