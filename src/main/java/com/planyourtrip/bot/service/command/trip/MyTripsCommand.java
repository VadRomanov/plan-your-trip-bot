package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.TripDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.impl.TripServiceImpl;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Collection;
import java.util.List;

import static com.planyourtrip.bot.constant.BotAnswer.MY_TRIPS_EMPTY_RESPONSE;
import static com.planyourtrip.bot.constant.BotAnswer.MY_TRIPS_INIT_RESPONSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyTripsCommand extends AbstractCommand {
    private final TripServiceImpl tripService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processInitResponse(commandDto.getTelegramId(), commandDto.getChatId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().length;
        if (step == 1) {
            return processInitResponse(callbackDto.getTelegramId(), callbackDto.getChatId());
        } else if (step == 2) {
            var tripId = Long.parseLong(callbackDto.getCallbackData()[1]);
            var trip = tripService.getTripById(tripId);
            return ResponseDto.builder()
                    .text(String.format("%s%s", trip.getName(), trip.getExpired() ? BotAnswer.EXPIRED : Strings.EMPTY))
                    .chatId(callbackDto.getChatId())
                    .keyboard(getActionsKeyboard(tripId))
                    .build();
        } else {
            return returnErrorMessage(callbackDto.getChatId());
        }
    }

    private ResponseDto processInitResponse(long telegramId, long chatId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? MY_TRIPS_EMPTY_RESPONSE : MY_TRIPS_INIT_RESPONSE)
                .chatId(chatId)
                .keyboard(trips.isEmpty() ? getNewTripKeyboard() : getTripsKeyboard(trips))
                .build();
    }

    private ReplyKeyboard getActionsKeyboard(long tripId) {
        return ReplyKeyboardBuilder.buildInlineKeyboard(List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_TRIP.getDescription(),
                        String.format("%s/%s", CommandType.EDIT_TRIP.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.DELETE_TRIP.getDescription(),
                        String.format("%s/%s", CommandType.DELETE_TRIP.getName(), tripId))
        ));
    }

    private ReplyKeyboard getTripsKeyboard(Collection<TripDto> trips) {
        return ReplyKeyboardBuilder.buildTripsInlineKeyboard(trips, CommandType.MY_TRIPS);
    }

    private ReplyKeyboard getNewTripKeyboard() {
        return ReplyKeyboardBuilder.buildInlineKeyboard(List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.NEW_TRIP.getDescription(),
                        CommandType.NEW_TRIP.getName())
        ));
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_TRIPS;
    }
}
