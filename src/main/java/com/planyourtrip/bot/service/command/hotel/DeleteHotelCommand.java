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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteHotelCommand extends AbstractCommand {
    private final TripService tripService;
    private final HotelService hotelService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return requestTripId(commandDto.getTelegramId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().length;
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestHotelId(Long.parseLong(callbackDto.getCallbackData()[1]));
        } else if (step == 3) {
            return requestConfirmation(Long.parseLong(callbackDto.getCallbackData()[1]));
        } else if (step == 4) {
            return processConfirmation(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_RESPONSE)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.DELETE_HOTEL))
                .build();
    }

    private ResponseDto requestHotelId(long tripId) {
        var hotels = hotelService.getHotelsByTripId(tripId);
        return ResponseDto.builder()
                .text(hotels.isEmpty() ? BotAnswer.MY_HOTELS_EMPTY_RESPONSE : BotAnswer.CHOOSE_HOTEL_RESPONSE)
                .keyboard(hotels.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_HOTEL)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(mapHotelsToMap(hotels), tripId,
                        CommandType.DELETE_HOTEL))
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
                                String.format("%s/%s/%s/%s", CommandType.DELETE_HOTEL.getName(), hotel.getTripId(),
                                        hotel.getId(), BotAnswer.CONFIRMED)),
                        new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.CANCEL, CommandType.CANCEL.getName())))
                .build();
    }

    private ResponseDto processConfirmation(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData()[3].equals(BotAnswer.CONFIRMED)) {
            return doDelete(Long.parseLong(callbackDto.getCallbackData()[2]));
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto doDelete(long hotelId) {
        hotelService.deleteHotel(hotelId);
        return ResponseDto.builder()
                .text(BotAnswer.DELETE_HOTEL_FINAL_RESPONSE)
                .build();
    }

    private Map<Long, String> mapHotelsToMap(Collection<HotelDto> hotels) {
        return hotels.stream()
                .collect(Collectors.toMap(HotelDto::getId, HotelDto::toString));
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_HOTEL;
    }
}
