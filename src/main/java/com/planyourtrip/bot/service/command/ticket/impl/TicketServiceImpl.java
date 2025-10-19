package com.planyourtrip.bot.service.command.ticket.impl;

import com.planyourtrip.bot.dto.domain.TicketDto;
import com.planyourtrip.bot.dto.domain.TicketType;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import com.planyourtrip.bot.service.command.ticket.TicketService;
import com.planyourtrip.bot.service.core.TicketCoreClient;
import com.planyourtrip.bot.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketCoreClient ticketCoreClient;

    private static final Map<Long, TicketDto.TicketDtoBuilder> TICKET_DTO_CHAT_CONTAINER = new ConcurrentHashMap<>();
    private static final Map<Long, TicketDto> TICKET_DTO_CHAT_UPDATE_CONTAINER = new ConcurrentHashMap<>();

    @Override
    public void createTicket(int type, long tripId, long chatId) {
        log.debug("Create ticket {}, chatId {}", type, chatId);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, TicketDto.builder()
                .tripId(tripId)
                .type(TicketType.findByCode(type)));
    }

    @Override
    public void setDeparture(String departure, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set ticket departure {}, chatId {}", departure, chatId);
        ticket.departure(departure);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticket);
    }

    @Override
    public void setArrival(String arrival, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set ticket arrival {}, chatId {}", arrival, chatId);
        ticket.arrival(arrival);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticket);
    }

    @Override
    public void setDepartDate(LocalDate date, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set ticket departure date {}, chatId {}", date, chatId);
        ticket.departureTime(DateTimeUtils.toOffsetDateTime(date));
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticket);
    }

    @Override
    public void setDepartTime(LocalTime time, long chatId) {
        var ticketBuilder = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        var ticket = ticketBuilder.build();
        log.debug("Set ticket departure time {}, chatId {}", time, chatId);
        var departureDt = DateTimeUtils.addTime(ticket.getDepartureTime(), time);
        if (nonNull(ticket.getArrivalTime()) && ticket.getArrivalTime().isBefore(departureDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        ticketBuilder.departureTime(departureDt);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticketBuilder);
    }

    @Override
    public void setArriveDate(LocalDate date, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set ticket arrival date {}, chatId {}", date, chatId);
        ticket.arrivalTime(DateTimeUtils.toOffsetDateTime(date));
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticket);
    }

    @Override
    public void setArriveTime(LocalTime time, long chatId) {
        var ticketBuilder = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        var ticket = ticketBuilder.build();
        log.debug("Set ticket arrival time {}, chatId {}", time, chatId);
        var arriveDt = DateTimeUtils.addTime(ticket.getArrivalTime(), time);
        if (nonNull(ticket.getDepartureTime()) && ticket.getDepartureTime().isAfter(arriveDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        ticketBuilder.arrivalTime(arriveDt);
        TICKET_DTO_CHAT_CONTAINER.put(chatId, ticketBuilder);
    }

    @Override
    public void setFileId(String fileId, long chatId) {
        var ticket = TICKET_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set file_id {}, chatId {}", fileId, chatId);
        ticket.fileUrl(fileId);
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
    public void fetchTicketById(long id, long chatId) {
        var ticket = getTicketById(id);
        TICKET_DTO_CHAT_UPDATE_CONTAINER.put(chatId, ticket);
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
        var savedTicket = ticketCoreClient.createTicket(ticket.build());
        TICKET_DTO_CHAT_CONTAINER.remove(chatId);

        log.info("Ticket {} for chatId {} commited", ticket, chatId);
        return savedTicket;
    }

    @Override
    public TicketDto getTicketToUpdate(long chatId) {
        return TICKET_DTO_CHAT_UPDATE_CONTAINER.get(chatId);
    }

    @Override
    public void updateTicket(TicketDto ticket, long chatId) {
        log.debug("Update ticket {}", ticket);
        var updatedTicket = ticketCoreClient.updateTicket(ticket.getId(), ticket);
        TICKET_DTO_CHAT_UPDATE_CONTAINER.remove(chatId);
        log.info("Ticket {} updated", updatedTicket);
    }
}
