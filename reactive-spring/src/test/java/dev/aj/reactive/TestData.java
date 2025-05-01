package dev.aj.reactive;

import dev.aj.reactive.domain.dtos.UserRequestDto;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestComponent;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    private final Faker faker;

    public Flux<UserRequestDto> userRequestDtoStream() {
        return Flux.fromStream(Stream.of(
                UserRequestDto.builder()
                        .firstName(faker.name().firstName())
                        .lastName(faker.name().lastName())
                        .email(faker.internet().emailAddress())
                        .password(faker.internet().password(8, 20, true, true, true))
                        .build()
        ));
    }

}
