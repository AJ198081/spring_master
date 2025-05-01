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

        userService.totalCount()
                .map(count -> {
                    if (count == 0) {
                        return testData.userRequestDtoStream()
                                .take(30)
                                .map(userRequestDto -> userService.createUser(Mono.just(userRequestDto)));
                    }
                    return count;
                })
//                .onErrorMap(throwable -> new RuntimeException(throwable.getCause().getLocalizedMessage()))
                .subscribe(count -> System.out.println("Total Users: " + count));
    }

}
