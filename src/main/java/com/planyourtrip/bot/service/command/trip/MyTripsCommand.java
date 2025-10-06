package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractMyCommand;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyTripsCommand extends AbstractMyCommand {
    private final TripService tripService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripIdAnswer(callbackDto.getTelegramId());
        } else if (step == 2) {
            return prepareAnswer(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    protected ResponseDto prepareAnswer(long id) {
        var trip = tripService.getTripById(id);
        return ResponseDto.builder()
                .text(trip.toString())
                .keyboard(ReplyKeyboardBuilder.buildActionToTripButton(id, CommandType.EDIT_TRIP,
                        CommandType.DELETE_TRIP))
                .build();
    }

    private ResponseDto requestTripIdAnswer(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        if (trips.isEmpty()) {
            return ResponseDto.builder()
                    .text(BotAnswer.MY_TRIPS_EMPTY_RESPONSE)
                    .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(CommandType.NEW_TRIP.getDescription(),
                            CommandType.NEW_TRIP.getName())))
                    .build();
        }
        return ResponseDto.builder()
                .text(BotAnswer.MY_TRIPS_RESPONSE)
                .keyboard(ReplyKeyboardBuilder.buildTripsButtons(trips, getCommandType()))
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_TRIPS;
    }
}
