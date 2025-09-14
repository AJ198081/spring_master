package dev.aj.full_stack_v6;

import dev.aj.full_stack_v6.common.domain.entities.Category;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.stream.Stream;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class TestDataFactory {

    private final Faker faker;

    public Stream<Category> getStreamOfCategories() {
        return Stream.of(Category.builder()
                .name(faker.commerce().department())
                .build());
    }

}
