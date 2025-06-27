package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddTicketCommand extends AbstractCommand {

    private final TicketService ticketService;
    private final TripService tripService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processCommandResponse(commandDto);
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return processCallbackResponse(callbackDto);
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (State.valueOf(messageDto.getState().getState())) {
            case AWAIT_DEPARTURE -> processDepartureResponse(messageDto);
            case AWAIT_DEPARTURE_DT -> processDepartureDtResponse(messageDto);
            case AWAIT_ARRIVAL -> processArrivalResponse(messageDto);
            case AWAIT_ARRIVAL_DT -> processArrivalDtResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
            case AWAIT_TRIP_ID, AWAIT_TYPE -> null;
        };
    }

    private ResponseDto processCommandResponse(CommandDto commandDto) {
        var trips = tripService.getTripsByTelegramId(commandDto.getTelegramId());
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.ADD_TICKET_COMMAND_RESPONSE)
                .keyboard(trips.isEmpty()
                        ? getNewTripKeyboard()
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.ADD_TICKET))
                .build();
    }

    private ResponseDto processCallbackResponse(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData().length == 2) {
            return processTripIdResponse(Long.parseLong(callbackDto.getCallbackData()[1]));
        } else {
            return processTypeResponse(callbackDto);
        }
    }

    private ResponseDto processTripIdResponse(long tripId) {
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_INIT_RESPONSE)
                .keyboard(getTickerTypesKeyboard(tripId))
                .build();
    }

    private ResponseDto processTypeResponse(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData()[1]);
        var ticketType = callbackDto.getCallbackData()[2];
        ticketService.createTicket(ticketType, tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_TICKET)
                .setState(State.AWAIT_DEPARTURE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_DEPARTURE_RESPONSE)
                .build();
    }

    private ResponseDto processDepartureResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        ticketService.setDeparture(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_TICKET)
                .setState(State.AWAIT_DEPARTURE_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_DEPARTURE_DT_RESPONSE)
                .build();
    }

    private ResponseDto processDepartureDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var departureDt = DateTimeUtils.parseDatetime(messageDto.getMsgText());
        ticketService.setDepartureDt(departureDt, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_TICKET)
                .setState(State.AWAIT_ARRIVAL.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_ARRIVAL_RESPONSE))
                .build();
    }

    private ResponseDto processArrivalResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        ticketService.setArrival(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_TICKET)
                .setState(State.AWAIT_ARRIVAL_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_ARRIVAL_DT_RESPONSE)
                .build();
    }

    private ResponseDto processArrivalDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var departureDt = DateTimeUtils.parseDatetime(messageDto.getMsgText());
        ticketService.setArrivalDt(departureDt, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_TICKET)
                .setState(State.AWAIT_FILE.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_FILE_RESPONSE))
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var trip = ticketService.commitNewTicket(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_FINAL_RESPONSE))
                .keyboard(getFinalKeyboard(trip.getId()))
                .build();
    }


    private List<ReplyKeyboardBuilder.KeyboardButton> getFinalKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_TRIP.getDescription(),
                        format("%s/%s", CommandType.EDIT_TRIP.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.MY_TRIPS.getDescription(),
                        CommandType.MY_TRIPS.getName())
        );
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getNewTripKeyboard() {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.NEW_TRIP.getDescription(),
                        CommandType.NEW_TRIP.getName())
        );
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getTickerTypesKeyboard(long tripId) {
        return ReplyKeyboardBuilder.buildButtons(
                Arrays.stream(TicketType.values()).map(TicketType::name).toList(),
                CommandType.ADD_TICKET,
                tripId);
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_TICKET;
    }

    private enum State {
        AWAIT_TRIP_ID, AWAIT_TYPE, AWAIT_DEPARTURE, AWAIT_DEPARTURE_DT, AWAIT_ARRIVAL, AWAIT_ARRIVAL_DT, AWAIT_FILE
    }
}
