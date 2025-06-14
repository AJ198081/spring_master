package dev.aj.full_stack_v5.auth.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {
    private String username;
    private String password;
    private Set<String> roles;
}
