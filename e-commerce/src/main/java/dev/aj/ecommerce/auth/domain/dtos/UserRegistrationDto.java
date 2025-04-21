package dev.aj.ecommerce.auth.domain.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegistrationDto {
    private String email;

    private String username;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 50 characters")
    private String password;

    private String role;
}
