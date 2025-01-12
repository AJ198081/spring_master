package dev.aj.sdj_hibernate.domain.services.impl.init;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.domain.entities.HotelEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class HotelEntities {

    private final Faker faker;

    public Stream<HotelEntity> getDummyHotels() {
        return Stream.generate(() -> HotelEntity.builder()
                .name(faker.company().name())
                .build());
    }

}
