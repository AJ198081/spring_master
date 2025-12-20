package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.security.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserGraphQLController {

    private final UserService userService;

    @MutationMapping
    public ResponseEntity<Void> createUser(@Argument("createUserDto") UserCreateRequest createUserDto, HttpServletRequest request) {

        Long newUserId = userService.createNewUser(createUserDto);

        var uri = ServletUriComponentsBuilder.fromContextPath(request)
                .path("/graphql") // Append your specific endpoint
                .queryParam("id", newUserId)
                .build()
                .toUri();   // Result: http://host:port/context-path/graphql?id=newUserId
        return ResponseEntity.created(uri).build();
    }

}
