package dev.aj.full_stack_v2.config.security.controller;

import dev.aj.full_stack_v2.config.security.jwt.JWTUtils;
import dev.aj.full_stack_v2.domain.dto.LoginRequest;
import dev.aj.full_stack_v2.domain.dto.LoginResponse;
import dev.aj.full_stack_v2.domain.entities.security.SecurityUser;
import dev.aj.full_stack_v2.services.impl.SecurityUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/public")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final SecurityUserService securityUserService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(path = "/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            log.error("Authentication Exception: {}", e.getMessage());
            Map<String, Object> map = new HashMap<>();
            map.put("error", "Invalid username or password");
            map.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityUser currentUser = (SecurityUser) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokeFromUserName(currentUser);

        List<String> currentUserRoles = currentUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).map(String::toString).toList();
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(jwtToken, currentUser.getUsername(), currentUserRoles));
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<String> registerUser(@RequestBody SecurityUser securityUser) {
        securityUser.setPassword(passwordEncoder.encode(securityUser.getPassword()));
        SecurityUser savedUser = securityUserService.saveUser(securityUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("%s created with ID %d".formatted(savedUser.getUsername(), savedUser.getId()));
    }

    @GetMapping(path = "/registered_users")
    public ResponseEntity<List<SecurityUser>> getRegisteredUsers(@AuthenticationPrincipal UserDetails currentUser) {

        log.info("{} requested registered users", currentUser.getUsername());

        List<SecurityUser> allUsersInDatabase = securityUserService.getAllUsersInDatabase();
        return ResponseEntity.status(HttpStatus.OK).body(allUsersInDatabase);
    }
}
