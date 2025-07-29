package dev.aj.full_stack_v5.auth.domain.mapper;

import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.auth.domain.entities.Role;
import dev.aj.full_stack_v5.auth.domain.entities.User;
import org.jspecify.annotations.NonNull;
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

    public static final String ROLE_NAME_PREFIX = "ROLE_";

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
                .map(this::roleNameMapper)
                .map(roleName -> Role.builder()
                        .name(roleName)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named(value = "mapSetRolesToSetStrings")
    public Set<String> mapSetRolesToSetStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(roleName -> roleName.substring(ROLE_NAME_PREFIX.length()))
                .collect(Collectors.toSet());
    }

    @Named(value = "roleNameMapper")
    public String roleNameMapper(@NonNull String roleName) {

        String trimmedRoleName = roleName.toUpperCase().trim();
        return trimmedRoleName.startsWith(ROLE_NAME_PREFIX)
                ? trimmedRoleName
                : ROLE_NAME_PREFIX.concat(validateNonEmptyOrThrow(trimmedRoleName));
    }

    private String validateNonEmptyOrThrow(@NonNull String roleName) {

        if (!roleName.isEmpty()) {
            return roleName;
        }

        throw new IllegalArgumentException("Role name must not be empty");
    }
}
