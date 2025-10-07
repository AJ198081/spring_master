package dev.aj.full_stack_v6.common.domain.mappers;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.common.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN
)
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    User userCreateRequestToUser(UserCreateRequest userCreateRequest);

    @SuppressWarnings("unused")
    @Mapping(target = "role", source = "user", qualifiedByName = "userRoleToString")
    UserCreateRequest userToUserCreateRequest(User user);

    default User patchUserFromUserCreateRequest(User existingUser, UserCreateRequest req, PasswordEncoder passwordEncoder) {

        if (req == null) {
            return existingUser;
        }

        if (req.email() != null) {
            existingUser.setEmail(req.email());
        }

        if (req.password() != null) {
            existingUser.setPassword(passwordEncoder.encode(req.password()));
        }

        return existingUser;
    }

    @Named("userRoleToString")
    default String userRoleToString(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No roles associated with user %s".formatted(user.getUsername())));
    }

}
