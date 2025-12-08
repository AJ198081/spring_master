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
            summary = "Create a new user",
            description = "Create a new user with the given credentials",
            method = "post",
            operationId = "createUser",
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
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/usernameTaken/{username}")
    @Operation(summary = "Check if a username is taken",
            description = "Check if a username is taken",
            method = "get",
            operationId = "isUsernameTaken",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "username",
                            schema = @Schema(maxLength = 10, minLength = 1))
            },
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Username taken?",
                    useReturnTypeSchema = true),
            }
    )
    public ResponseEntity<Boolean> isUsernameTaken(@PathVariable String username) {
        return ResponseEntity.ok(userService.exists(username));
    }

    @DeleteMapping("/{username}")
    @Operation(
            method = "delete",
            description = "Delete a username",
            operationId = "deleteUser",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "username", schema = @Schema(maxLength = 10, minLength = 1))
            },
            responses = {@ApiResponse(responseCode = "202", description = "User deleted")}
    )
    public ResponseEntity<Void> deleteUser(@PathVariable String username, Principal principal) {

        userService.deleteUser(username, principal);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/")
    @Operation(
            summary = "Update a user (PUT operation)",
            method = "put",
            operationId = "updateUser",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User to update",
                    required = true,
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateRequest.class))
                    }
            ),
            responses = {
                    @ApiResponse(description = "User updated successfully", responseCode = "202")
            })
    public ResponseEntity<Void> updateUser(@RequestBody UserCreateRequest userCreateRequest, Principal principal) {
        userService.updateUser(userCreateRequest, principal);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{username}/password")
    @Operation(
            summary = "Change a user's password",
            method = "patch",
            operationId = "changePassword",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "username", required = true, schema = @Schema(maxLength = 10, minLength = 1)),
                    @Parameter(in = ParameterIn.QUERY, name = "password", required = true, schema = @Schema(maxLength = 100, minLength = 8))
            },
            responses = {@ApiResponse(responseCode = "202", description = "Password changed")}
    )
    public ResponseEntity<Void> changePassword(@PathVariable String username,
                                               @RequestParam String password,
                                               Principal principal) {

        userService.changePassword(username, password, principal);
        return ResponseEntity.accepted().build();
    }
}
