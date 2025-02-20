package dev.aj.full_stack_v3.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.aj.full_stack_v3.domain.dto.validations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;

@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class UserRegistrationRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @JsonProperty("firstname")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @JsonProperty("lastname")
    private String lastName;

    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    private String username;

    @Size(min = 2, max = 50, message = "Email must be between 2 and 50 characters")
    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @ValidPassword(passwordValidityRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,50}$")
    private String password;
}
