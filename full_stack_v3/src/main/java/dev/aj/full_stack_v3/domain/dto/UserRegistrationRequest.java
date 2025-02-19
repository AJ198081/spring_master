package dev.aj.full_stack_v3.domain.dto;

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
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
}
