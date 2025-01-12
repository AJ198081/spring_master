package dev.aj.sdj_hibernate.domain.entities.mappers;

import dev.aj.sdj_hibernate.domain.entities.Ticket;
import dev.aj.sdj_hibernate.domain.entities.dtos.TicketDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketMapper {

    @Mapping(target = "auditMetaData", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    Ticket ticketDtoToTicket(TicketDto ticketDto);

}
