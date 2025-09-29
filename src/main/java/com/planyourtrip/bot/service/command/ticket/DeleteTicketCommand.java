package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.TicketDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.TripService;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteTicketCommand extends AbstractCommand {
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
            return requestConfirmation(Long.parseLong(callbackDto.getCallbackData()[2]));
        } else if (step == 4) {
            return processConfirmation(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_RESPONSE)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.DELETE_TICKET))
                .build();
    }

    private ResponseDto requestTicketId(long tripId) {
        var tickets = ticketService.getTicketsByTripId(tripId);
        return ResponseDto.builder()
                .text(tickets.isEmpty() ? BotAnswer.MY_TICKETS_EMPTY_RESPONSE : BotAnswer.DELETE_TICKET_INIT_RESPONSE)
                .keyboard(tickets.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_TICKET)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(mapTicketsToMap(tickets), tripId,
                        CommandType.DELETE_TICKET))
                .build();
    }

    private ResponseDto requestConfirmation(long ticketId) {
        var ticket = ticketService.getTicketById(ticketId);
        return ResponseDto.builder()
                .text(String.format(BotAnswer.DELETE_TICKET_CONFIRMATION_REQUEST, ticket))
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(
                                BotAnswer.DELETE_CONFIRMATION_BUTTON,
                                String.format("%s/%s/%s/%s", CommandType.DELETE_TICKET.getName(), ticket.getTripId(),
                                        ticket.getId(), BotAnswer.CONFIRMED)),
                        new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.CANCEL, CommandType.CANCEL.getName())))
                .build();
    }

    private ResponseDto processConfirmation(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData()[3].equals(BotAnswer.CONFIRMED)) {
            return doDelete(Long.parseLong(callbackDto.getCallbackData()[2]));
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto doDelete(long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseDto.builder()
                .text(BotAnswer.DELETE_TICKET_FINAL_RESPONSE)
                .build();
    }

    private Map<Long, String> mapTicketsToMap(Collection<TicketDto> tickets) {
        return tickets.stream()
                .collect(Collectors.toMap(TicketDto::getId, TicketDto::toString));
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_TICKET;
    }
}
