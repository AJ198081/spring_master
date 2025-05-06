package dev.aj.reactive.domain.mappers;

import dev.aj.reactive.domain.dtos.UserRequestDto;
import dev.aj.reactive.domain.dtos.UserResponseDto;
import dev.aj.reactive.domain.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        imports = {PasswordEncoder.class}
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", qualifiedByName = "bcryptEncodeString")
    public abstract User toEntity(UserRequestDto dto);

    public abstract UserResponseDto toDto(User user);

    @Named("bcryptEncodeString")
    public String bcryptEncodeString(String password) {
        return passwordEncoder.encode(password);
    }

}
