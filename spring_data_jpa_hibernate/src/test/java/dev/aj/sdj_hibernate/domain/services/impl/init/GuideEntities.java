package dev.aj.sdj_hibernate.domain.services.impl.init;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.domain.entities.GuideEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class GuideEntities {

    private final Faker faker;

    public Stream<GuideEntity> getStreamOfGuideEntities() {
        return Stream.generate(() -> GuideEntity.builder()
                .name(faker.name().fullName())
                .staffId(faker.bothify("A??#####", true))
                .salary(new BigDecimal(faker.numerify("#####.##")))
                .build());
    }

}
