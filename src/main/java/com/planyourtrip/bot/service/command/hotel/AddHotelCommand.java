package com.planyourtrip.bot.service.command.hotel;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.AccommodationType;
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

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddHotelCommand extends AbstractCommand {
    private final HotelService hotelService;
    private final TripService tripService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processCommandResponse(commandDto);
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return processCallbackResponse(callbackDto);
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (State.valueOf(messageDto.getState().getState())) {
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_ADDRESS -> processAddressResponse(messageDto);
            case AWAIT_CHECK_IN -> processCheckInResponse(messageDto);
            case AWAIT_CHECK_OUT -> processCheckOutResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
            case AWAIT_TRIP_ID, AWAIT_TYPE -> null;
        };
    }

    private ResponseDto processCommandResponse(CommandDto commandDto) {
        var trips = tripService.getTripsByTelegramId(commandDto.getTelegramId());
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.ADD_HOTEL_COMMAND_RESPONSE)
                .keyboard(trips.isEmpty()
                        ? getNewTripKeyboard()
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.ADD_HOTEL))
                .build();
    }

    private ResponseDto processCallbackResponse(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData().length == 2) {
            return processTripIdResponse(Long.parseLong(callbackDto.getCallbackData()[1]));
        } else {
            return processTypeResponse(callbackDto);
        }
    }

    private ResponseDto processTripIdResponse(long tripId) {
        return ResponseDto.builder()
                .text(BotAnswer.ADD_HOTEL_INIT_RESPONSE)
                .keyboard(getAccommodationTypesKeyboard(tripId))
                .build();
    }

    private ResponseDto processTypeResponse(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData()[1]);
        var accommodationCode = Integer.parseInt(callbackDto.getCallbackData()[2]);
        hotelService.createHotel(accommodationCode, tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(State.AWAIT_NAME.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_HOTEL_NAME_RESPONSE)
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        hotelService.setName(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(State.AWAIT_ADDRESS.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_HOTEL_ADDRESS_RESPONSE)
                .build();
    }

    private ResponseDto processAddressResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        hotelService.setAddress(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(State.AWAIT_CHECK_IN.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_HOTEL_CHECK_IN_RESPONSE))
                .build();
    }

    private ResponseDto processCheckInResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var checkInDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        hotelService.setCheckInDate(checkInDate, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(State.AWAIT_CHECK_OUT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_HOTEL_CHECK_OUT_RESPONSE)
                .build();
    }

    private ResponseDto processCheckOutResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var checkOutDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        hotelService.setCheckOutDate(checkOutDate, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(State.AWAIT_FILE.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_HOTEL_FILE_RESPONSE))
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var hotel = hotelService.commitNewHotel(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_HOTEL_FINAL_RESPONSE))
                .keyboard(getFinalKeyboard(hotel.getTripId()))
                .build();
    }


    private List<ReplyKeyboardBuilder.KeyboardButton> getFinalKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_TRIP.getDescription(),
                        format("%s/%s", CommandType.EDIT_TRIP.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.MY_TRIPS.getDescription(),
                        CommandType.MY_TRIPS.getName())
        );
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getNewTripKeyboard() {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.NEW_TRIP.getDescription(),
                        CommandType.NEW_TRIP.getName())
        );
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getAccommodationTypesKeyboard(long tripId) {
        return ReplyKeyboardBuilder.buildButtons(
                Arrays.stream(AccommodationType.values()).toList(),
                CommandType.ADD_HOTEL,
                tripId);
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_HOTEL;
    }

    private enum State {
        AWAIT_TRIP_ID, AWAIT_TYPE, AWAIT_NAME, AWAIT_ADDRESS, AWAIT_CHECK_IN, AWAIT_CHECK_OUT, AWAIT_FILE
    }
}
