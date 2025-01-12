package dev.aj.sdj_hibernate.domain.services;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.Item;
import dev.aj.sdj_hibernate.domain.repositories.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.stream.Stream;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties", properties = {
        "logging.level.root=off",
        "logging.level.org.springframework.transaction=trace",
        "logging.level.org.hibernate.engine.transaction.internal.TransactionImpl=DEBUG",
        "logging.level.org.springframework.orm.jpa.JpaTransactionManager=trace",
        "spring.jpa.properties.hibernate.show_sql=true"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(value = {PostgresConfiguration.class})
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private Faker faker;


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void persistItem() {
        streamOfSampleItems().limit(1)
                .forEach(itemService::persistItem);

        Assertions.assertThat(itemRepository.count()).isGreaterThan(0L);
    }

    private Stream<Item> streamOfSampleItems() {
        return Stream.generate(() -> Item.builder()
                .name(faker.book().title())
                .price(BigDecimal.valueOf(faker.random().nextInt(10, 100)))
                .build());
    }
}