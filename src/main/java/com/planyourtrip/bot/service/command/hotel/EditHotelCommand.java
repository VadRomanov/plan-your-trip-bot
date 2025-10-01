package com.planyourtrip.bot.service.command.hotel;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.AccommodationType;
import com.planyourtrip.bot.service.command.hotel.util.HotelUtil;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.TripService;
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

import static java.util.Objects.nonNull;

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
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestHotelId(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else if (step == 3) {
            return processHotelIdResponse(callbackDto);
        } else if (step == 4) {
            return requestNewValue(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (HotelUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TYPE -> processTypeResponse(messageDto);
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_CHECK_IN -> processCheckInDateResponse(messageDto);
            case AWAIT_CHECK_OUT -> processCheckOutDateResponse(messageDto);
            case AWAIT_ADDRESS -> processAddressResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
        };
    }

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.EDIT_HOTEL))
                .build();
    }

    private ResponseDto requestHotelId(long tripId) {
        var hotels = hotelService.getHotelsByTripId(tripId);
        return ResponseDto.builder()
                .text(hotels.isEmpty() ? BotAnswer.MY_HOTELS_EMPTY_RESPONSE : BotAnswer.CHOOSE_HOTEL_REQUEST)
                .keyboard(hotels.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_HOTEL)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(HotelUtil.mapHotelsToMap(hotels), tripId,
                        CommandType.EDIT_HOTEL))
                .build();
    }

    private ResponseDto processHotelIdResponse(CallbackDto callbackDto) {
        long tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        long hotelId = Long.parseLong(callbackDto.getCallbackData().get(2));
        return ResponseDto.builder()
                .text(BotAnswer.EDIT_CHOOSE_FIELD_REQUEST)
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(HotelUtil.State.AWAIT_TYPE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        HotelUtil.State.AWAIT_TYPE)),
                        new ReplyKeyboardBuilder.KeyboardButton(HotelUtil.State.AWAIT_NAME.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        HotelUtil.State.AWAIT_NAME)),
                        new ReplyKeyboardBuilder.KeyboardButton(HotelUtil.State.AWAIT_CHECK_IN.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        HotelUtil.State.AWAIT_CHECK_IN)),
                        new ReplyKeyboardBuilder.KeyboardButton(HotelUtil.State.AWAIT_CHECK_OUT.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        HotelUtil.State.AWAIT_CHECK_OUT)),
                        new ReplyKeyboardBuilder.KeyboardButton(HotelUtil.State.AWAIT_ADDRESS.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_HOTEL.getName(), tripId, hotelId,
                                        HotelUtil.State.AWAIT_ADDRESS))))
                .build();
    }

    private ResponseDto requestNewValue(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var hotelId = Long.parseLong(callbackDto.getCallbackData().get(2));
        var newValue = HotelUtil.State.valueOf(callbackDto.getCallbackData().get(3));
        hotelService.fetchHotelById(hotelId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.EDIT_HOTEL)
                .setState(newValue.name()));
        if (newValue.equals(HotelUtil.State.AWAIT_TYPE)) {
            return ResponseDto.builder()
                    .text(BotAnswer.ADD_HOTEL_TYPE_REQUEST)
                    .keyboard(HotelUtil.getAccommodationTypesKeyboard(tripId))
                    .build();
        }
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_REQUEST)
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

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var hotel = hotelService.getHotelToUpdate(chatId);
        if (nonNull(messageDto.getDocument().getFileId())) {
            hotelService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
        hotelService.updateHotel(hotel, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_HOTEL;
    }

}
