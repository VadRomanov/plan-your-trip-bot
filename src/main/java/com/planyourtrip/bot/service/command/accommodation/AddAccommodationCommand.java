package com.planyourtrip.bot.service.command.accommodation;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.accommodation.util.AccommodationUtil;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddAccommodationCommand extends AbstractCommand {
    private final AccommodationService accommodationService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return processTripIdResponse(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else if (step == 3) {
            return processTypeResponse(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (AccommodationUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_ADDRESS -> processAddressResponse(messageDto);
            case AWAIT_CHECK_IN -> processCheckInResponse(messageDto);
            case AWAIT_CHECK_OUT -> processCheckOutResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
            case AWAIT_TYPE -> returnErrorMessage();
        };
    }

    private ResponseDto processTripIdResponse(long tripId) {
        return ResponseDto.builder()
                .text(BotAnswer.ADD_ACCOMMODATION_TYPE_REQUEST)
                .keyboard(AccommodationUtil.getAccommodationTypesKeyboard(tripId))
                .build();
    }

    private ResponseDto processTypeResponse(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var accommodationCode = Integer.parseInt(callbackDto.getCallbackData().get(2));
        accommodationService.createAccommodation(accommodationCode, tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(AccommodationUtil.State.AWAIT_NAME.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_ACCOMMODATION_NAME_REQUEST)
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        accommodationService.setName(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(AccommodationUtil.State.AWAIT_ADDRESS.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_ACCOMMODATION_ADDRESS_REQUEST)
                .build();
    }

    private ResponseDto processAddressResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        accommodationService.setAddress(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(AccommodationUtil.State.AWAIT_CHECK_IN.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_ACCOMMODATION_CHECK_IN_REQUEST))
                .build();
    }

    private ResponseDto processCheckInResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var checkInDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        accommodationService.setCheckInDate(checkInDate, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(AccommodationUtil.State.AWAIT_CHECK_OUT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_ACCOMMODATION_CHECK_OUT_REQUEST)
                .build();
    }

    private ResponseDto processCheckOutResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var checkOutDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        accommodationService.setCheckOutDate(checkOutDate, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(AccommodationUtil.State.AWAIT_FILE.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_ACCOMMODATION_FILE_REQUEST))
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        if (nonNull(messageDto.getDocument().getFileId())) {
            accommodationService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
        var accommodation = accommodationService.commitNewAccommodation(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_ACCOMMODATION_FINAL_RESPONSE))
                .keyboard(getFinalKeyboard(accommodation.getTripId()))
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_ACCOMMODATION;
    }

}
