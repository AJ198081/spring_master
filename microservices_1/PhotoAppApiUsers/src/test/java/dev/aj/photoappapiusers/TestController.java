package dev.aj.photoappapiusers;

import dev.aj.photoappapiusers.domain.entity.User;
import dev.aj.photoappapiusers.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
class TestController {

    private final UserRepository userRepository;

    @RequestMapping("/user")
    public ResponseEntity<UserCredentials> getUserCredentials() {
        User user = userRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("No users found"));
        UserCredentials userCredentials = new UserCredentials(user.getUsername(), InitDB.TEST_PASSWORD);
        return ResponseEntity.ok(userCredentials);
    }

}

record UserCredentials(String username, String password) {}
