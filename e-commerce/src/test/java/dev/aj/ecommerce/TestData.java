package dev.aj.ecommerce;

import com.github.javafaker.Faker;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    private final Faker faker;

    @Value("${password.special-characters: '!@#$%^'}")
    private char[] specialCharacters;



    private @Size(min = 8, message = "Password must be at least eight characters") String getValidPassword() {
        String password = faker.internet().password(8, 50, true, true, true);

        if (!StringUtils.containsAny(password, specialCharacters)) {
            password = password.substring(0, password.length() - 1)
                    .concat(String.valueOf(specialCharacters[faker.random().nextInt(specialCharacters.length)]));
        }

        return password;
    }


}
