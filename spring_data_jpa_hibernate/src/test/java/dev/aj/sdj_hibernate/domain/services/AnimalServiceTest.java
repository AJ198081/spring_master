package dev.aj.sdj_hibernate.domain.services;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.Animal;
import dev.aj.sdj_hibernate.domain.entities.Cat;
import dev.aj.sdj_hibernate.domain.entities.Dog;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditingConfig;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@Import(value = {PostgresConfiguration.class,
        AnimalService.class,
        Faker.class,
        AuditingConfig.class,
})
@TestPropertySource(locations = {"/application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnimalServiceTest {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private Faker faker;

    @ParameterizedTest
    @MethodSource(value = "getAnimals")
    void saveAnAnimal(Animal animal, Class<?> clazz) {

        Animal persistedAnimal = animalService.saveAnAnimal(animal);

        assertThat(persistedAnimal, notNullValue());
        assertThat(persistedAnimal.getId(), notNullValue());
        assertThat(persistedAnimal, instanceOf(clazz));
        assertThat(persistedAnimal.getName(), notNullValue());
    }

    public Stream<Arguments> getAnimals() {
        return Stream.of(
                Arguments.of(Cat.builder()
//                                .type(faker.cat().breed())
                                .name(faker.cat().name())
                                .age(faker.random().nextInt(1, 12))
                                .color(faker.color().name())
                        .build(), Cat.class),

                Arguments.of(Dog.builder()
                                .type(faker.dog().breed())
                                .name(faker.dog().name())
                                .color(faker.color().name())
                                .age(faker.random().nextInt(1, 18))
                        .build(), Dog.class));
    }
}