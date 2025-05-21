package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.TripDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.impl.TripServiceImpl;
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
        return processInitResponse(commandDto.getUserId(), commandDto.getChatId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData().length == 1) {
            return processInitResponse(callbackDto.getUserId(), callbackDto.getChatId());
        } else {
            var trip = tripService.getTripById(Long.parseLong(callbackDto.getCallbackData()[1]));
            return ResponseDto.builder()
                    .text(String.format("%s%s", trip.getName(), trip.getExpired() ? " (Завершено)" : Strings.EMPTY))
                    .chatId(callbackDto.getChatId())
                    .build();
        }
    }

    private ResponseDto processInitResponse(long userId, long chatId) {
        var trips = tripService.getTripsByUserId(userId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? MY_TRIPS_EMPTY_RESPONSE : MY_TRIPS_INIT_RESPONSE)
                .chatId(chatId)
                .keyboard(trips.isEmpty() ? getNewTripKeyboard() : getTripsKeyboard(trips))
                .build();
    }

    private ReplyKeyboard getActionsKeyboard(TripDto trip) {
        return ReplyKeyboardBuilder.buildInlineKeyboard(List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_TRIP.getDescription(),
                        String.format("%s/%s", CommandType.EDIT_TRIP.getName(), trip.getId())),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.DELETE_TRIP.getDescription(),
                        String.format("%s/%s", CommandType.DELETE_TRIP.getName(), trip.getId()))
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
