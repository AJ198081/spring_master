package dev.aj.sdj_hibernate.domain.services.impl;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.Actor;
import dev.aj.sdj_hibernate.domain.entities.Movie;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditingConfig;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@Import(value = {PostgresConfiguration.class, MovieServiceImpl.class, Faker.class, AuditingConfig.class})
@TestPropertySource(locations = {"/application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "logging.level.org.hibernate.orm.jdbc.bind=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovieServiceImplTest {

    @Autowired
    private MovieServiceImpl movieService;

    @Autowired
    private Faker faker;

    @Test
    void persistMovieAndActors() {

        Movie movie = movieService.persistMovieAndActors(
                getDummyMovies().limit(1).findFirst().orElseThrow(),
                getDummyActors().limit(10).toList()
        );

        Assertions.assertThat(movie).isNotNull()
                .extracting("actors")
                .isNotNull();

        Movie fetchedMovie = movieService.findAMovie();

        Assertions.assertThat(fetchedMovie).isNotNull()
                .extracting("actors")
                .asInstanceOf(InstanceOfAssertFactories.SET)
                .allSatisfy(actor -> Assertions.assertThat(((Actor) actor).getId()).isNotNull())
                .allSatisfy(actor -> Assertions.assertThat(((Actor) actor).getName()).isNotNull());

        assertThat(fetchedMovie, notNullValue());
        assertThat(fetchedMovie.getActors().stream().map(Actor::getName).toList(),
                everyItem(notNullValue()));
    }

    @ParameterizedTest
    @MethodSource(value = "getMovieAndActors")
    void persistMultipleMoviesAndActorsSuccessfully(Movie movie, List<Actor> actors) {
        Movie persistedMovie = movieService.persistMovieAndActors(movie, actors);

        assertThat(persistedMovie, notNullValue());
        assertThat(persistedMovie.getId(), notNullValue());
        assertThat(persistedMovie.getActors().stream().map(Actor::getName).toList(), everyItem(notNullValue()));
    }

    private Stream<Arguments> getMovieAndActors() {
        return Stream.of(
                Arguments.of(getDummyMovies().limit(1).findFirst().orElseThrow(),
                        getDummyActors().limit(5).toList()),
                Arguments.of(getDummyMovies().limit(1).findFirst().orElseThrow(),
                        getDummyActors().limit(5).toList())
        );
    }

    private Stream<Actor> getDummyActors() {
        return Stream.generate(() -> Actor.builder()
                .name(faker.hobbit().character())
                .build());
    }

    private Stream<Movie> getDummyMovies() {
        return Stream.generate(() -> Movie.builder()
                .name(faker.harryPotter().spell())
                .build());
    }
}