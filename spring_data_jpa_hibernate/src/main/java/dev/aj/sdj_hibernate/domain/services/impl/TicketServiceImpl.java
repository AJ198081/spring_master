package dev.aj.sdj_hibernate.domain.services.impl;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.domain.entities.Ticket;
import dev.aj.sdj_hibernate.domain.entities.dtos.TicketDto;
import dev.aj.sdj_hibernate.domain.entities.mappers.TicketMapper;
import dev.aj.sdj_hibernate.domain.repositories.TicketRepository;
import dev.aj.sdj_hibernate.domain.services.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final Faker faker = new Faker(Locale.of("en", "IND"));

    @Transactional
    @Override
    public Ticket createTicket(TicketDto ticketDto) throws IllegalStateException {
        Ticket newTicket = ticketMapper.ticketDtoToTicket(ticketDto);
        newTicket.setFlightNumber(faker.number().digits(4));
        newTicket.setPrice(BigDecimal.valueOf(Math.random() * 200 + 50));
        Ticket issuedTicket = ticketRepository.save(newTicket);


        Long currentTicketId = issuedTicket.getId();

        if (currentTicketId % 2 == 0) {
            throw new IllegalStateException("Ticket creation failed");
        }
        return issuedTicket;
    }


    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @SneakyThrows
    @Transactional
    @Override
    public void testHikariConnections(TicketDto ticketDto) {
        Ticket ticketingRequest = ticketMapper.ticketDtoToTicket(ticketDto);

//        Hikari publishes connections stats every 30 seconds
        TimeUnit.SECONDS.sleep(40);

        Ticket issuedTicket = ticketRepository.save(ticketingRequest);

        TimeUnit.SECONDS.sleep(40);

        log.info("Ticket issued: {}", issuedTicket);
    }

    // readOnly ensures hibernate doesn't do 'dirty checking' at the end of a transaction, hence runtime leverage optimisations
    @Transactional(readOnly = false)
    @Override
    public Long testDirtyCheckingAndAutoCommitting() {
        Ticket firstAvailableTicket = ticketRepository.findAll().getFirst();
        log.info("First available ticket: {}", firstAvailableTicket);
        BigDecimal updatedPrice = firstAvailableTicket.getPrice().add(BigDecimal.TEN);
        firstAvailableTicket.setPrice(updatedPrice); // won't update if 'readOnly' true
//        ticketRepository.save(firstAvailableTicket); // Even this won't work for 'readOnly' transactions
//        ticketRepository.flush();
        return firstAvailableTicket.getId();
    }
}
