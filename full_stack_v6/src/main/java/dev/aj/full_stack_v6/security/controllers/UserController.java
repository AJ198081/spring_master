package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.security.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("${USER_API_PATH}")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PostMapping("/")
    @Operation(
            tags = {"AuthN"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User to create",
                    required = true,
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserCreateRequest.class, requiredMode = Schema.RequiredMode.REQUIRED))
                    }
            ),
            responses = {@ApiResponse(responseCode = "201", description = "User created")}
    )
    public ResponseEntity<Void> saveUser(@RequestBody UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/usernameTaken/{username}")
    @Operation(summary = "Check if a username is taken", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Username taken?",
                    useReturnTypeSchema = true
            ),
    })
    public ResponseEntity<Boolean> usernameTaken(@PathVariable
                                                 @Parameter(in = ParameterIn.PATH,
                                                         schema = @Schema(maxLength = 10, minLength = 1)) String username
    ) {
        return ResponseEntity.ok(userService.exists(username));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Parameter(in = ParameterIn.PATH) String username,
                                           Principal principal) {
        userService.deleteUser(username, principal);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/")
    public ResponseEntity<Void> updateUser(@RequestBody UserCreateRequest userCreateRequest, Principal principal) {
        userService.updateUser(userCreateRequest, principal);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String username,
                                               @RequestParam @Parameter(in = ParameterIn.QUERY) String password,
                                               Principal principal) {
        userService.changePassword(username, password, principal);
        return ResponseEntity.accepted().build();
    }
}
