package dev.aj.full_stack_v3.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationResponse {

    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
