package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.dto.TicketDto;

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

    void deleteTicket(long id);

    TicketDto commitNewTicket(long chatId);

}
