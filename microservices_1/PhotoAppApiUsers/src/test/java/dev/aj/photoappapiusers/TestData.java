package dev.aj.photoappapiusers;

import com.github.javafaker.Faker;
import dev.aj.photoappapiusers.domain.dto.UserRegistrationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    private final Faker faker;

    public Stream<UserRegistrationRequestDto> getUserRegistrationStream() {
        return Stream.generate(() -> UserRegistrationRequestDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .username(faker.name().username())
                .password(faker.lorem().characters(8, 40, true, true))
                .role("USER")
                .build()
        );
    }

}
