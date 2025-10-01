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

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyTicketsCommand extends AbstractCommand {
    private final TripService tripService;
    private final TicketService ticketService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processInitResponse(commandDto.getTelegramId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return processInitResponse(callbackDto.getTelegramId());
        } else if (step == 2) {
            return prepareAnswer(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto processInitResponse(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.MY_TICKETS))
                .build();
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getActionsKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_TICKET.getDescription(),
                        String.format("%s/%s", CommandType.EDIT_TICKET.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.DELETE_TICKET.getDescription(),
                        String.format("%s/%s", CommandType.DELETE_TICKET.getName(), tripId))
        );
    }

    private ResponseDto prepareAnswer(long tripId) {
        var tickets = ticketService.getTicketsByTripId(tripId);
        if (tickets.isEmpty()) {
            return ResponseDto.builder()
                    .text(BotAnswer.MY_TICKETS_EMPTY_RESPONSE)
                    .keyboard(getAddTicketKeyboard(tripId))
                    .build();
        }
        return ResponseDto.builder()
                .text(String.format("""
                                <b>Список билетов:
                                %s</b>
                                """,
                        String.join(",",
                                tickets.stream()
                                        .map(TicketDto::toString)
                                        .toList())))
                .keyboard(getActionsKeyboard(tripId))
                .build();
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getAddTicketKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.ADD_TICKET.getDescription(),
                        format("%s/%s", CommandType.ADD_TICKET.getName(), tripId))
        );
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_TICKETS;
    }
}
