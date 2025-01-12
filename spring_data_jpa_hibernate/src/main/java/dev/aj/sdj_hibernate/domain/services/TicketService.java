package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.Ticket;
import dev.aj.sdj_hibernate.domain.entities.dtos.TicketDto;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TicketService {
    @Transactional
    Ticket createTicket(TicketDto ticketDto) throws IllegalStateException;

    List<Ticket> getAllTickets();

    @SneakyThrows
    @Transactional
    void testHikariConnections(TicketDto ticketDto);

    // readOnly ensures hibernate doesn't do 'dirty checking' at the end of a transaction, hence runtime leverage optimisations
    @Transactional(readOnly = false)
    Long testDirtyCheckingAndAutoCommitting();
}
