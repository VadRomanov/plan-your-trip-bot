package com.planyourtrip.bot.service.command.ticket.impl;

import com.planyourtrip.bot.dto.TicketDto;
import com.planyourtrip.bot.dto.TicketType;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import com.planyourtrip.bot.service.command.ticket.TicketService;
import com.planyourtrip.bot.service.core.TicketCoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketCoreClient ticketCoreClient;

    private static final Map<Long, TicketDto> TICKET_DTO_CHAT_CONTAINER = new ConcurrentHashMap<>();

    @Override
    public void createTicket(String type, long tripId, long chatId) {
        log.debug("Create ticket {}, chatId {}", type, chatId);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, new TicketDto(tripId).setType(TicketType.valueOf(type)));
    }

    @Override
    public void setDeparture(String departure, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set ticket departure {}, chatId {}", departure, chatId);
        ticket.setDeparture(departure);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticket);
    }

    @Override
    public void setArrival(String arrival, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set ticket arrival {}, chatId {}", arrival, chatId);
        ticket.setArrival(arrival);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticket);
    }

    @Override
    public void setDepartureDt(LocalDateTime departureDt, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set ticket departure datetime {}, chatId {}", departureDt, chatId);
        if (nonNull(ticket.getArrivalTime()) && ticket.getArrivalTime().isBefore(departureDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        ticket.setDepartureTime(departureDt);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticket);
    }

    @Override
    public void setArrivalDt(LocalDateTime arrivalDt, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set ticket arrival datetime {}, chatId {}", arrivalDt, chatId);
        if (nonNull(ticket.getDepartureTime()) && ticket.getDepartureTime().isAfter(arrivalDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        ticket.setArrivalTime(arrivalDt);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticket);
    }

    @Override
    public Collection<TicketDto> getTicketsByTripId(long tripId) {
        log.debug("Get tickets by tripId {}", tripId);
        var tickets = ticketCoreClient.getTicketsByTrip(tripId);
        log.info("{} tickets obtained by tripId {}", tickets.size(), tripId);
        return tickets;
    }

    @Override
    public TicketDto getTicketById(long id) {
        log.debug("Get ticket by id {}", id);
        var ticket = ticketCoreClient.getTicketById(id);
        log.info("Ticket obtained by id {}", id);
        return ticket;
    }

    @Override
    public void deleteTicket(long id) {
        log.debug("Delete ticket by id {}", id);
        ticketCoreClient.deleteTicket(id);
        log.info("Ticket deleted by id {}", id);
    }

    public TicketDto commitNewTicket(long chatId) {
        log.debug("Commit ticket for chatId {}", chatId);
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        var savedTicket = ticketCoreClient.createTicket(ticket);
        TICKET_DTO_CHAT_CONTAINER.remove(chatId);

        log.info("Ticket {} for chatId {} commited", ticket, chatId);
        return savedTicket;
    }
}
