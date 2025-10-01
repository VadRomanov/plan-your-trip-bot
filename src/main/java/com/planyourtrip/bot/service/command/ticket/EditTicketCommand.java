package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.TicketType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.ticket.util.TicketUtil;
import com.planyourtrip.bot.service.command.trip.TripService;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestTicketId(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else if (step == 3) {
            return processTicketIdResponse(callbackDto);
        } else if (step == 4) {
            return requestNewValue(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (TicketUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TYPE -> processTypeResponse(messageDto);
            case AWAIT_DEPARTURE -> processDepartureResponse(messageDto);
            case AWAIT_ARRIVAL -> processArrivalResponse(messageDto);
            case AWAIT_DEPART_DT -> processDepartTimeResponse(messageDto);
            case AWAIT_ARRIVE_DT -> processArriveTimeResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
        };
    }

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.EDIT_TICKET))
                .build();
    }

    private ResponseDto requestTicketId(long tripId) {
        var tickets = ticketService.getTicketsByTripId(tripId);
        return ResponseDto.builder()
                .text(tickets.isEmpty() ? BotAnswer.MY_TICKETS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TICKET_REQUEST)
                .keyboard(tickets.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_TICKET)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(TicketUtil.mapTicketsToMap(tickets), tripId,
                        CommandType.EDIT_TICKET))
                .build();
    }

    private ResponseDto processTicketIdResponse(CallbackDto callbackDto) {
        long tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        long ticketId = Long.parseLong(callbackDto.getCallbackData().get(2));
        return ResponseDto.builder()
                .text(BotAnswer.EDIT_CHOOSE_FIELD_REQUEST)
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(TicketUtil.State.AWAIT_TYPE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        TicketUtil.State.AWAIT_TYPE)),
                        new ReplyKeyboardBuilder.KeyboardButton(TicketUtil.State.AWAIT_DEPARTURE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        TicketUtil.State.AWAIT_DEPARTURE)),
                        new ReplyKeyboardBuilder.KeyboardButton(TicketUtil.State.AWAIT_ARRIVAL.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        TicketUtil.State.AWAIT_ARRIVAL)),
                        new ReplyKeyboardBuilder.KeyboardButton(TicketUtil.State.AWAIT_DEPART_DT.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        TicketUtil.State.AWAIT_DEPART_DT)),
                        new ReplyKeyboardBuilder.KeyboardButton(TicketUtil.State.AWAIT_ARRIVE_DT.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        TicketUtil.State.AWAIT_ARRIVE_DT)),
                        new ReplyKeyboardBuilder.KeyboardButton(TicketUtil.State.AWAIT_FILE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_TICKET.getName(), tripId, ticketId,
                                        TicketUtil.State.AWAIT_FILE))))
                .build();
    }

    private ResponseDto requestNewValue(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var ticketId = Long.parseLong(callbackDto.getCallbackData().get(2));
        var newValue = TicketUtil.State.valueOf(callbackDto.getCallbackData().get(3));
        ticketService.fetchTicketById(ticketId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.EDIT_TICKET)
                .setState(newValue.name()));
        if (newValue.equals(TicketUtil.State.AWAIT_TYPE)) {
            return ResponseDto.builder()
                    .text(BotAnswer.ADD_TICKET_TYPE_REQUEST)
                    .keyboard(TicketUtil.getTickerTypesKeyboard(tripId))
                    .build();
        }
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_REQUEST)
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

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_TICKET;
    }

}
