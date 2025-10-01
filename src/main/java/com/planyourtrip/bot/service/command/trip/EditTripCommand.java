package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.util.TripUtil;
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

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditTripCommand extends AbstractCommand {

    private final TripService tripService;

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
            return processTripIdResponse(callbackDto);
        } else if (step == 3) {
            return requestNewValue(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (TripUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_START_DT -> processStartDtResponse(messageDto);
            case AWAIT_END_DT -> processEndDtResponse(messageDto);
        };
    }

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.EDIT_TRIP))
                .build();
    }

    private ResponseDto processTripIdResponse(CallbackDto callbackDto) {
        long tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        return ResponseDto.builder()
                .text(BotAnswer.EDIT_CHOOSE_FIELD_REQUEST)
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(TripUtil.State.AWAIT_NAME.getValue(),
                                String.format("%s/%s/%s", CommandType.EDIT_NOTE.getName(), tripId,
                                        TripUtil.State.AWAIT_NAME)),
                        new ReplyKeyboardBuilder.KeyboardButton(TripUtil.State.AWAIT_START_DT.getValue(),
                                String.format("%s/%s/%s", CommandType.EDIT_NOTE.getName(), tripId,
                                        TripUtil.State.AWAIT_START_DT)),
                        new ReplyKeyboardBuilder.KeyboardButton(TripUtil.State.AWAIT_END_DT.getValue(),
                                String.format("%s/%s/%s", CommandType.EDIT_NOTE.getName(), tripId,
                                        TripUtil.State.AWAIT_END_DT)),
                        new ReplyKeyboardBuilder.KeyboardButton(CommandType.EDIT_TICKET.getDescription(),
                                format("%s/%s", CommandType.EDIT_TICKET.getName(), tripId)),
                        new ReplyKeyboardBuilder.KeyboardButton(CommandType.EDIT_HOTEL.getDescription(),
                                format("%s/%s", CommandType.EDIT_HOTEL.getName(), tripId)),
                        new ReplyKeyboardBuilder.KeyboardButton(CommandType.EDIT_NOTE.getDescription(),
                                format("%s/%s", CommandType.EDIT_NOTE.getName(), tripId))))
                .build();
    }

    private ResponseDto requestNewValue(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        long tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var newValue = TripUtil.State.valueOf(callbackDto.getCallbackData().get(2));
        tripService.fetchTripById(tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.EDIT_TRIP)
                .setState(newValue.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_REQUEST)
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var trip = tripService.getTripToUpdate(chatId);
        trip.setName(messageDto.getMsgText());
        tripService.updateTrip(trip, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processStartDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var trip = tripService.getTripToUpdate(chatId);
        var startDt = DateTimeUtils.parseDate(messageDto.getMsgText());
        trip.setStartDate(startDt);
        tripService.updateTrip(trip, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processEndDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var trip = tripService.getTripToUpdate(chatId);
        var endDt = DateTimeUtils.parseDate(messageDto.getMsgText());
        trip.setEndDate(endDt);
        tripService.updateTrip(trip, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_TRIP;
    }

}
