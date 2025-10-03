package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.dto.domain.TripDto;
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
            return prepareAnswer(callbackDto.getTelegramId());
        } else {
            return returnErrorMessage();
        }
    }


    @Override
    protected ResponseDto prepareAnswer(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        if (trips.isEmpty()) {
            return ResponseDto.builder()
                    .text(BotAnswer.MY_TRIPS_EMPTY_RESPONSE)
                    .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(CommandType.NEW_TRIP.getDescription(),
                            CommandType.NEW_TRIP.getName())))
                    .build();
        }
        return ResponseDto.builder()
                .text(String.format(BotAnswer.MY_TRIPS_RESPONSE,
                        String.join(",",
                                trips.stream()
                                        .map(TripDto::toString)
                                        .toList())))
                .keyboard(getActionsKeyboard())
                .build();
    }

    protected List<ReplyKeyboardBuilder.KeyboardButton> getActionsKeyboard() {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(CommandType.EDIT_TRIP.getDescription(),
                        CommandType.EDIT_TRIP.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(CommandType.DELETE_TRIP.getDescription(),
                        CommandType.DELETE_TRIP.getName())
        );

    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_TRIPS;
    }
}
