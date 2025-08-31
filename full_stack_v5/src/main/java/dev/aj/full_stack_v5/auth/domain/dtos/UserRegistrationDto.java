package dev.aj.full_stack_v5.auth.domain.dtos;

import dev.aj.full_stack_v5.auth.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import static dev.aj.full_stack_v5.auth.domain.enums.UserType.REGULAR;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {
    private String username;
    private String password;
    private Set<String> roles;

    @Builder.Default
    private UserType userType = REGULAR;
}
