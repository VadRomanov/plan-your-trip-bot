package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.TicketDto;
import com.planyourtrip.bot.dto.TicketType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.TripService;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditTicketCommand extends AbstractCommand {

    private final TripService tripService;
    private final TicketService ticketService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return requestTripId(commandDto.getTelegramId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().length;
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestTicketId(Long.parseLong(callbackDto.getCallbackData()[1]));
        } else if (step == 3) {
            return processTicketIdResponse(callbackDto);
        } else if (step == 4) {
            return requestNewValue(callbackDto.getChatId(), State.valueOf(callbackDto.getCallbackData()[3]));
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TYPE -> processTypeResponse(messageDto);
            case AWAIT_DEPARTURE -> processDepartureResponse(messageDto);
            case AWAIT_ARRIVAL -> processArrivalResponse(messageDto);
            case AWAIT_DEPART_TIME -> processDepartTimeResponse(messageDto);
            case AWAIT_ARRIVE_TIME -> processArriveTimeResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
        };
    }

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_RESPONSE)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.EDIT_TICKET))
                .build();
    }

    private ResponseDto requestTicketId(long tripId) {
        var tickets = ticketService.getTicketsByTripId(tripId);
        return ResponseDto.builder()
                .text(tickets.isEmpty() ? BotAnswer.MY_TICKETS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TICKET_RESPONSE)
                .keyboard(tickets.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_TICKET)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(mapTicketsToMap(tickets), tripId,
                        CommandType.EDIT_TICKET))
                .build();
    }

    private ResponseDto processTicketIdResponse(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        long tripId = Long.parseLong(callbackDto.getCallbackData()[1]);
        long ticketId = Long.parseLong(callbackDto.getCallbackData()[2]);
        ticketService.fetchTicketById(ticketId, chatId);
        return ResponseDto.builder()
                .text(BotAnswer.EDIT_CHOOSE_FIELD_RESPONSE)
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_TYPE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        State.AWAIT_TYPE)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_DEPARTURE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        State.AWAIT_DEPARTURE)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_ARRIVAL.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        State.AWAIT_ARRIVAL)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_DEPART_TIME.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        State.AWAIT_DEPART_TIME)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_ARRIVE_TIME.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        State.AWAIT_ARRIVE_TIME)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_FILE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        State.AWAIT_FILE))))
                .build();
    }

    private ResponseDto requestNewValue(long chatId, State newValue) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.EDIT_TICKET)
                .setState(newValue.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_RESPONSE)
                .build();
    }

    private ResponseDto processTypeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        ticket.setType(TicketType.valueOf(messageDto.getMsgText()));
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processDepartureResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        ticket.setDeparture(messageDto.getMsgText());
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processArrivalResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        ticket.setArrival(messageDto.getMsgText());
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processDepartTimeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        var departureDt = DateTimeUtils.parseDatetime(messageDto.getMsgText());
        ticket.setDepartureTime(departureDt);
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processArriveTimeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        var arrivalDt = DateTimeUtils.parseDatetime(messageDto.getMsgText());
        ticket.setArrivalTime(arrivalDt);
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        if (nonNull(messageDto.getDocument().getFileId())) {
            ticketService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private Map<Long, String> mapTicketsToMap(Collection<TicketDto> tickets) {
        return tickets.stream()
                .collect(Collectors.toMap(TicketDto::getId, TicketDto::toString));
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_TICKET;
    }

    @Getter
    @RequiredArgsConstructor
    private enum State {
        AWAIT_TYPE("Тип"),
        AWAIT_DEPARTURE("Место отправления"),
        AWAIT_ARRIVAL("Место прибытия"),
        AWAIT_DEPART_TIME("Дата и время отправления"),
        AWAIT_ARRIVE_TIME("Дата и время прибытия"),
        AWAIT_FILE("Файл");

        private final String value;
    }
}
