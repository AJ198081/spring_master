package dev.aj.reactive;

import dev.aj.reactive.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import reactor.core.publisher.Mono;

@TestComponent
@RequiredArgsConstructor
public class InitDatabase {

    private final TestData testData;
    private final UserService userService;

    @PostConstruct
    public void init() {

        Mono<Long> numberOfUsers = userService.totalCount();

        numberOfUsers
                .map(count -> {
                    if (count == 0) {

                        testData.userRequestDtoStream()
                                .map(userRequestDto -> userService.createUser(Mono.just(userRequestDto)))
                                .take(30)
                                .subscribe();

                        return count;
                    }
                    return count;
                })
                .onErrorMap(throwable -> new RuntimeException(throwable.getCause().getLocalizedMessage()));
    }

}
