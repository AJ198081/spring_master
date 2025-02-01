package dev.aj.full_stack_v2.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v2.domain.entities.security.SecurityUser;
import dev.aj.full_stack_v2.services.impl.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SecurityUserService securityUserService;

    private final ObjectMapper objectMapper;

    @GetMapping(path = "/users")
//    @Secured(value = {"ROLE_ADMIN", "ROLE_USER"})
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")  // Both are synonymous
    public ResponseEntity<List<SecurityUser>> getUsers() {
        List<SecurityUser> users = securityUserService.getAllUsersInDatabase();

        String jsonResponse;
        List<SecurityUser> securityUsers;

        try {
            jsonResponse = objectMapper.writeValueAsString(users);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            securityUsers = objectMapper.readValue(jsonResponse, new TypeReference<List<SecurityUser>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        users = securityUsers;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-User-Count", String.valueOf(users.size()));

        return ResponseEntity.ok().headers(httpHeaders).body(users);
    }

    @PostMapping(path = "user")
    public ResponseEntity<SecurityUser> createUser(@RequestBody SecurityUser securityUser) {
        return ResponseEntity.ok(securityUserService.saveUser(securityUser));
    }

    @PutMapping(path = "user/{username}")
    public ResponseEntity<SecurityUser> updateUser(@RequestBody SecurityUser userToBeUpdated, @PathVariable String username) {
        return ResponseEntity.ok(securityUserService.updateUserByUsername(username, userToBeUpdated));
    }

    @PutMapping("role")
    public ResponseEntity<String> updateUserRole(@RequestParam String username, @RequestParam String role, @AuthenticationPrincipal SecurityUser currentUser) {
        String updateUserRole = securityUserService.updateUserRole(username, role);
        return ResponseEntity.ok(updateUserRole);
    }
}
