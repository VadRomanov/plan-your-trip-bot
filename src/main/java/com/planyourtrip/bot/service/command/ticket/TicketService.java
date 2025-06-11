package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.dto.TicketDto;

import java.time.LocalDateTime;
import java.util.Collection;

public interface TicketService {

    void createTicket(String type, long tripId, long chatId);

    void setDeparture(String departure, long chatId);

    void setArrival(String arrival, long chatId);

    void setDepartureDt(LocalDateTime departureDt, long chatId);

    void setArrivalDt(LocalDateTime arrivalDt, long chatId);

    Collection<TicketDto> getTicketsByTripId(long tripId);

    TicketDto getTicketById(long id);

    void deleteTicket(long id);

    TicketDto commitNewTicket(long chatId);

}
