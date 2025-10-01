package com.planyourtrip.bot.service.command.hotel;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.HotelDto;
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
public class MyHotelsCommand extends AbstractCommand {
    private final TripService tripService;
    private final HotelService hotelService;

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
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.MY_HOTELS))
                .build();
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getActionsKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_HOTEL.getDescription(),
                        String.format("%s/%s", CommandType.EDIT_HOTEL.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.DELETE_HOTEL.getDescription(),
                        String.format("%s/%s", CommandType.DELETE_HOTEL.getName(), tripId))
        );
    }

    private ResponseDto prepareAnswer(long tripId) {
        var hotels = hotelService.getHotelsByTripId(tripId);
        if (hotels.isEmpty()) {
            return ResponseDto.builder()
                    .text(BotAnswer.MY_HOTELS_EMPTY_RESPONSE)
                    .keyboard(getAddHotelKeyboard(tripId))
                    .build();
        }
        return ResponseDto.builder()
                .text(String.format("""
                                <b>Список отелей:
                                %s</b>
                                """,
                        String.join(",",
                                hotels.stream()
                                        .map(HotelDto::toString)
                                        .toList())))
                .keyboard(getActionsKeyboard(tripId))
                .build();
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getAddHotelKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.ADD_HOTEL.getDescription(),
                        format("%s/%s", CommandType.ADD_HOTEL.getName(), tripId))
        );
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_HOTELS;
    }
}
