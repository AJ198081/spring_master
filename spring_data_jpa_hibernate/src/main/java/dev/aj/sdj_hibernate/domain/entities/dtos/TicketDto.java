package dev.aj.sdj_hibernate.domain.entities.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TicketDto {

    private String flightNumber;
    private String name;
    private String departure;
    private String destination;

}
