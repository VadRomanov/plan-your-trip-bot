package com.planyourtrip.bot.service.command.hotel;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.AccommodationType;
import com.planyourtrip.bot.dto.HotelDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.TripService;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.Getter;
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
public class EditHotelCommand extends AbstractCommand {

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
            return processHotelIdResponse(callbackDto);
        } else if (step == 4) {
            return requestNewValue(callbackDto.getChatId(), State.valueOf(callbackDto.getCallbackData()[3]));
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TYPE -> processTypeResponse(messageDto);
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_CHECK_IN -> processCheckInDateResponse(messageDto);
            case AWAIT_CHECK_OUT -> processCheckOutDateResponse(messageDto);
            case AWAIT_ADDRESS -> processAddressResponse(messageDto);
        };
    }

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_HOTEL_RESPONSE)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.EDIT_HOTEL))
                .build();
    }

    private ResponseDto requestHotelId(long tripId) {
        var hotels = hotelService.getHotelsByTripId(tripId);
        return ResponseDto.builder()
                .text(hotels.isEmpty() ? BotAnswer.MY_HOTELS_EMPTY_RESPONSE : BotAnswer.CHOOSE_HOTEL_RESPONSE)
                .keyboard(hotels.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_HOTEL)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(mapHotelsToMap(hotels), tripId,
                        CommandType.EDIT_HOTEL))
                .build();
    }

    private ResponseDto processHotelIdResponse(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        long tripId = Long.parseLong(callbackDto.getCallbackData()[1]);
        long hotelId = Long.parseLong(callbackDto.getCallbackData()[2]);
        hotelService.fetchHotelById(hotelId, chatId);
        return ResponseDto.builder()
                .text(BotAnswer.EDIT_CHOOSE_FIELD_RESPONSE)
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_TYPE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        State.AWAIT_TYPE)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_NAME.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        State.AWAIT_NAME)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_CHECK_IN.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        State.AWAIT_CHECK_IN)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_CHECK_OUT.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        State.AWAIT_CHECK_OUT)),
                        new ReplyKeyboardBuilder.KeyboardButton(State.AWAIT_ADDRESS.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        State.AWAIT_ADDRESS))))
                .build();
    }

    private ResponseDto requestNewValue(long chatId, State newValue) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.EDIT_HOTEL)
                .setState(newValue.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_RESPONSE)
                .build();
    }

    private ResponseDto processTypeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var hotel = hotelService.getHotelToUpdate(chatId);
        hotel.setType(AccommodationType.valueOf(messageDto.getMsgText()));
        hotelService.updateHotel(hotel, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var hotel = hotelService.getHotelToUpdate(chatId);
        hotel.setName(messageDto.getMsgText());
        hotelService.updateHotel(hotel, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processCheckInDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var hotel = hotelService.getHotelToUpdate(chatId);
        var checkOutDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        hotel.setCheckInDate(checkOutDate);
        hotelService.updateHotel(hotel, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processCheckOutDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var hotel = hotelService.getHotelToUpdate(chatId);
        var checkOutDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        hotel.setCheckOutDate(checkOutDate);
        hotelService.updateHotel(hotel, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processAddressResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var hotel = hotelService.getHotelToUpdate(chatId);
        hotel.setAddress(messageDto.getMsgText());
        hotelService.updateHotel(hotel, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private Map<Long, String> mapHotelsToMap(Collection<HotelDto> hotels) {
        return hotels.stream()
                .collect(Collectors.toMap(HotelDto::getId, HotelDto::toString));
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_HOTEL;
    }

    @Getter
    @RequiredArgsConstructor
    private enum State {
        AWAIT_TYPE("Тип"),
        AWAIT_NAME("Название"),
        AWAIT_CHECK_IN("Дата заселения"),
        AWAIT_CHECK_OUT("Дата выезда"),
        AWAIT_ADDRESS("Адрес");

        private final String value;
    }
}
