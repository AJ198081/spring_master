package dev.aj.full_stack_v3.domain.mapper;

import dev.aj.full_stack_v3.config.security.SecurityConfig;
import dev.aj.full_stack_v3.domain.dto.UserLoginRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationResponse;
import dev.aj.full_stack_v3.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {SecurityConfig.class}
)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    UserRegistrationResponse userToUserRegistrationResponse(User user);

    @Mapping(target = "password", qualifiedByName = "encodePassword")
    User userRegistrationRequestToUser(UserRegistrationRequest userRegistrationRequest);

    default UsernamePasswordAuthenticationToken userLoginRequestToUsernamePasswordAuthenticationToken(UserLoginRequest userLoginRequest){
        return new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword(), null);
    }
}
