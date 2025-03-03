package dev.aj.photoappapiusers.domain.mappers;

import dev.aj.photoappapiusers.domain.dto.SecurityUser;
import dev.aj.photoappapiusers.domain.dto.UserRegistrationRequestDto;
import dev.aj.photoappapiusers.domain.dto.UserRegistrationResponseDto;
import dev.aj.photoappapiusers.domain.entity.User;
import dev.aj.photoappapiusers.utils.CredentialsEncoder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Locale;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = CredentialsEncoder.class)
public interface UserMapper {

    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "encryptedPassword", source = "password", qualifiedByName = "encryptPassword")
    @Mapping(target = "role", qualifiedByName = "roleMapper")
    User dtoToEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    UserRegistrationResponseDto entityToDto(User user);

    @Mapping(target = "password", source = "encryptedPassword")
    SecurityUser userToSecurityUser(User user);

    @Named("roleMapper")
    default String roleMapper(String role) {
        if (role == null) {
            return "ROLE_USER";
        }
        if (role.toUpperCase(Locale.ROOT).startsWith("ROLE_")) {
            return role.toUpperCase(Locale.ROOT);
        }

        return "ROLE_".concat(role.toUpperCase(Locale.ROOT));
    }
}
