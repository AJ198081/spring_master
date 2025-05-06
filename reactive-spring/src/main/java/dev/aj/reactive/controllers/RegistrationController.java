package dev.aj.reactive.controllers;

import dev.aj.reactive.domain.dtos.UserRequestDto;
import dev.aj.reactive.domain.dtos.UserResponseDto;
import dev.aj.reactive.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Mono<UserResponseDto>> createUser(@RequestBody @Valid Mono<UserRequestDto> userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{ID}")
    public ResponseEntity<Mono<UserResponseDto>> getUserById(@PathVariable(name = "ID") UUID id) {

        return new ResponseEntity<>(userService.getUserById(Mono.just(id)), HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Flux<UserResponseDto>> getAllUsers(@RequestParam("page") int page, @RequestParam("size") int size) {
        return ResponseEntity.ok(userService.getUsersPage(PageRequest.of(page, size)));
    }

}
