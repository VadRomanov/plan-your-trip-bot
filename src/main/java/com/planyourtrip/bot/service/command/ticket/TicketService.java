package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.dto.domain.TicketDto;

import java.time.OffsetDateTime;
import java.util.Collection;

public interface TicketService {

    void createTicket(int code, long tripId, long chatId);

    void setDeparture(String departure, long chatId);

    void setArrival(String arrival, long chatId);

    void setDepartureDt(OffsetDateTime departureDt, long chatId);

    void setArrivalDt(OffsetDateTime arrivalDt, long chatId);

    void setFileId(String fileId, long chatId);

    Collection<TicketDto> getTicketsByTripId(long tripId);

    TicketDto getTicketById(long id);

    void fetchTicketById(long id, long chatId);

    void deleteTicket(long id);

    TicketDto commitNewTicket(long chatId);

    TicketDto getTicketToUpdate(long chatId);

    void updateTicket(TicketDto ticket, long chatId);
}
