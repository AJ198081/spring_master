package dev.aj.photoappapiusers.utils;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CredentialsEncoder {

    private final PasswordEncoder passwordEncoder;

    @Named("encryptPassword")
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

}
