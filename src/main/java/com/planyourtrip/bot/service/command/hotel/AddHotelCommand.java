package com.planyourtrip.bot.service.command.hotel;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
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

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddHotelCommand extends AbstractCommand {
    private final HotelService hotelService;
    private final TripService tripService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        var trips = tripService.getTripsByTelegramId(commandDto.getTelegramId());
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_HOTEL_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.ADD_HOTEL))
                .build();
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData().size() == 2) {
            return processTripIdResponse(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else {
            return processTypeResponse(callbackDto);
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (HotelUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_ADDRESS -> processAddressResponse(messageDto);
            case AWAIT_CHECK_IN -> processCheckInResponse(messageDto);
            case AWAIT_CHECK_OUT -> processCheckOutResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
            case AWAIT_TYPE -> null;
        };
    }

    private ResponseDto processTripIdResponse(long tripId) {
        return ResponseDto.builder()
                .text(BotAnswer.ADD_HOTEL_TYPE_REQUEST)
                .keyboard(HotelUtil.getAccommodationTypesKeyboard(tripId))
                .build();
    }

    private ResponseDto processTypeResponse(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var accommodationCode = Integer.parseInt(callbackDto.getCallbackData().get(2));
        hotelService.createHotel(accommodationCode, tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(HotelUtil.State.AWAIT_NAME.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_HOTEL_NAME_REQUEST)
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        hotelService.setName(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(HotelUtil.State.AWAIT_ADDRESS.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_HOTEL_ADDRESS_REQUEST)
                .build();
    }

    private ResponseDto processAddressResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        hotelService.setAddress(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(HotelUtil.State.AWAIT_CHECK_IN.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_HOTEL_CHECK_IN_REQUEST))
                .build();
    }

    private ResponseDto processCheckInResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var checkInDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        hotelService.setCheckInDate(checkInDate, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(HotelUtil.State.AWAIT_CHECK_OUT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_HOTEL_CHECK_OUT_REQUEST)
                .build();
    }

    private ResponseDto processCheckOutResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var checkOutDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        hotelService.setCheckOutDate(checkOutDate, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_HOTEL)
                .setState(HotelUtil.State.AWAIT_FILE.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_HOTEL_FILE_REQUEST))
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        if (nonNull(messageDto.getDocument().getFileId())) {
            hotelService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
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

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_HOTEL;
    }

}
