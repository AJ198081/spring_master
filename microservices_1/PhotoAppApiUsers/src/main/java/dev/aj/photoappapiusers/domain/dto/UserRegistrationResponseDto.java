package dev.aj.photoappapiusers.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationResponseDto {

    private String userId;

    private String firstName;
    private String lastName;
    private String email;

    private String username;
    private String role;
}
