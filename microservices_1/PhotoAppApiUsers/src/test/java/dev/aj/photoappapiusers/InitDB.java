package dev.aj.photoappapiusers;

import dev.aj.photoappapiusers.domain.dto.UserRegistrationRequestDto;
import dev.aj.photoappapiusers.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
@Slf4j
public class InitDB {

    private final TestData testData;
    private final UserService userService;

    @PostConstruct
    public void registerUsers() {

        UserRegistrationRequestDto userRegistrationRequestDto = testData.getUserRegistrationStream().limit(1).toList().stream().findFirst().orElseThrow();

        userService.saveUser(userRegistrationRequestDto);

        log.info("User {} with Credentials {} registered", userRegistrationRequestDto.getUsername(), userRegistrationRequestDto.getPassword());

    }
}
