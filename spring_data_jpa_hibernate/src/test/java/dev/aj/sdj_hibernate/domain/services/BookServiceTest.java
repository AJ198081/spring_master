package dev.aj.sdj_hibernate.domain.services;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.Book;
import dev.aj.sdj_hibernate.domain.entities.Ebook;
import dev.aj.sdj_hibernate.domain.entities.Paperback;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditingConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@Import(value = {BookService.class, PostgresConfiguration.class, Faker.class, AuditingConfig.class})
@TestPropertySource(locations = {"/application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private Faker faker;

    @Test
    void saveABook() {

        getStreamOfEBooks().limit(1).forEach(bookService::saveABook);

        getStreamOfPaperbackBooks().limit(1)
                .forEach(bookService::saveABook);

        List<Book> allAvailableBooks = bookService.getAllBooks();
        assertThat(allAvailableBooks, hasSize(2));

    }

    private Stream<Book> getStreamOfEBooks() {
        return Stream.generate(() -> Ebook
                .builder()
                .title(faker.book().title())
                .author(faker.book().author())
                .isbn(faker.code().isbn13())
                .publisher(faker.book().publisher())
                .build());
    }

    private Stream<Book> getStreamOfPaperbackBooks() {

        Random random = new Random();

        return Stream.generate(() -> Paperback
                .builder()
                .title(faker.book().title())
                .author(faker.book().author())
                .isbn(faker.code().isbn13())
                .publisher(faker.book().publisher())
                .size(faker.random().nextInt(200, 1050).toString())
                .weight(String.valueOf(random.nextDouble(0.250, 2.6)))
                .build());
    }
}