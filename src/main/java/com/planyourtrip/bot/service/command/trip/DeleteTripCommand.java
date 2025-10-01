package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteTripCommand extends AbstractCommand {
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
            return requestConfirmation(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else if (step == 3) {
            return processConfirmation(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.DELETE_TRIP))
                .build();
    }

    private ResponseDto requestConfirmation(long tripId) {
        var trip = tripService.getTripById(tripId);
        return ResponseDto.builder()
                .text(String.format(BotAnswer.DELETE_TRIP_CONFIRMATION_REQUEST, trip.getName(),
                        trip.getExpired() ? BotAnswer.EXPIRED : Strings.EMPTY))
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(
                                BotAnswer.DELETE_CONFIRMATION_REQUEST,
                                String.format("%s/%s/%s", CommandType.DELETE_TRIP.getName(), trip.getId(),
                                        BotAnswer.CONFIRMED)),
                        new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.CANCEL, CommandType.CANCEL.getName())))
                .build();
    }

    private ResponseDto processConfirmation(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData().get(2).equals(BotAnswer.CONFIRMED)) {
            return doDelete(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto doDelete(long tripId) {
        tripService.deleteTrip(tripId);
        return ResponseDto.builder()
                .text(BotAnswer.DELETE_TRIP_FINAL_RESPONSE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_TRIP;
    }
}
