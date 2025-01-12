package dev.aj.sdj_hibernate.domain.services;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.Ticket;
import dev.aj.sdj_hibernate.domain.entities.dtos.TicketDto;
import dev.aj.sdj_hibernate.domain.repositories.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Stream;

@Import(value = {PostgresConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties", properties = {
        "logging.level.com.zaxxer.hikari=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private Faker faker;

    @BeforeEach
    public void setUp() {
        try {
            ticketService.createTicket(streamOfSampleTicketDtos().limit(1)
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Bad joojoo happened")));
        } catch (IllegalStateException e) {
            System.out.println("Ok to catch, and ignore");
        }
    }

    private @NotNull Stream<TicketDto> streamOfSampleTicketDtos() {
        return Stream.generate(() -> TicketDto.builder()
                .name(faker.name().fullName())
                .destination(faker.address().city())
                .departure(faker.address().city())
                .build());
    }

    @AfterAll
    void tearDown() {
        ticketRepository.deleteAll();
    }

    @RepeatedTest(1)
    void createTicket() {
        List<Ticket> tickets = ticketService.getAllTickets();
        Assertions.assertThat(tickets).hasSizeLessThan(7);
    }

    @Test
    void testHikariConnections() {
        ticketService.testHikariConnections(streamOfSampleTicketDtos()
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bad joojoo happened")));
    }

    @Test
    void testDirtyCheckingAndAutoCommitting() {
        Long updatedTicketId = ticketService.testDirtyCheckingAndAutoCommitting();
        Ticket updatedTicket = ticketRepository.findById(updatedTicketId).orElseThrow(() -> new IllegalArgumentException("Bad joojoo happened"));
        log.info("Updated ticket: {}", updatedTicket.getPrice());

    }
}