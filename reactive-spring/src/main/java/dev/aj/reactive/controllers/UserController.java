package dev.aj.reactive.controllers;

import dev.aj.reactive.domain.dtos.UserRequestDto;
import dev.aj.reactive.domain.dtos.UserResponseDto;
import dev.aj.reactive.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<UserResponseDto>> createUser(@RequestBody @Valid Mono<UserRequestDto> userRequest) {

        return userService.createUser(userRequest)
                .map(userResponse -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .location(URI.create("/users/%s".formatted(userResponse.getId())))
                        .body(userResponse))
                .onErrorMap(DuplicateKeyException.class, e -> new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()))
                .onErrorMap(throwable -> {
                    if (throwable instanceof DataIntegrityViolationException) {
                        return new ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.getMessage());
                    } else {
                        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
                    }
                });

    }

    @GetMapping(value = "/{ID}")
    public Mono<ResponseEntity<UserResponseDto>> getAllUsers(@PathVariable(name = "ID", required = true) UUID id) {
        return Mono.just(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("ID is required"))) // Not that it can be empty because the framework will take care of it
                .flatMap(i -> userService.getUserById(Mono.just(i)))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Flux<UserResponseDto>> getAllUsers(@RequestParam("page") int page, @RequestParam("size") int size) {
        return ResponseEntity.ok(userService.getUsersPage(PageRequest.of(page, size)));
    }

}
