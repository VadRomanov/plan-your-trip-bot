package com.planyourtrip.bot.service.command.hotel;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.impl.TripServiceImpl;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteHotelCommand extends AbstractCommand {
    private final TripServiceImpl tripService;
    private final HotelService hotelService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processInitResponse(commandDto.getTelegramId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().length;
        if (step == 1) {
            return processInitResponse(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestConfirmation(Long.parseLong(callbackDto.getCallbackData()[1]));
        } else if (step == 3) {
            return processConfirmation(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto processInitResponse(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.DELETE_HOTEL_INIT_RESPONSE)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.DELETE_HOTEL))
                .build();
    }

    private ResponseDto requestConfirmation(long hotelId) {
        var hotel = hotelService.getHotelById(hotelId);
        return ResponseDto.builder()
                .text(String.format(BotAnswer.DELETE_HOTEL_CONFIRMATION_REQUEST,
                        hotel.getName(), hotel.getCheckInDate(), hotel.getCheckOutDate()))
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(
                                BotAnswer.DELETE_CONFIRMATION_BUTTON,
                                String.format("%s/%s/%s", CommandType.DELETE_HOTEL.getName(), hotel.getId(),
                                        BotAnswer.CONFIRMED)),
                        new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.CANCEL, CommandType.CANCEL.getName())))
                .build();
    }

    private ResponseDto processConfirmation(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData()[2].equals(BotAnswer.CONFIRMED)) {
            return doDelete(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto doDelete(CallbackDto callbackDto) {
        tripService.deleteTrip(Long.parseLong(callbackDto.getCallbackData()[1]));
        return ResponseDto.builder()
                .text(BotAnswer.DELETE_TRIP_FINAL_RESPONSE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_HOTEL;
    }
}
