package com.planyourtrip.bot.service.command.accommodation;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.accommodation.util.AccommodationUtil;
import com.planyourtrip.bot.service.command.impl.AbstractAddCommand;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddAccommodationCommand extends AbstractAddCommand {
    private final AccommodationService accommodationService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            var stepValue = callbackDto.getCallbackData().get(1);
            if (COMPLETE_SKIP_VALUES.contains(stepValue)) {
                return processSkipOrCompleteResponse(callbackDto.getChatId(), stepValue);
            } else {
                return processTripIdResponse(Long.parseLong(stepValue));
            }
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
                .keyboard(ReplyKeyboardBuilder.buildSkipAndCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processAddressResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        accommodationService.setAddress(messageDto.getMsgText(), chatId);
        return prepareCheckInRequest(chatId);
    }

    private ResponseDto prepareCheckInRequest(long chatId) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(AccommodationUtil.State.AWAIT_CHECK_IN.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_ACCOMMODATION_CHECK_IN_REQUEST))
                .keyboard(ReplyKeyboardBuilder.buildSkipAndCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processCheckInResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var checkInDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        accommodationService.setCheckInDate(checkInDate, chatId);
        return prepareCheckOutRequest(chatId);
    }

    private ResponseDto prepareCheckOutRequest(long chatId) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(AccommodationUtil.State.AWAIT_CHECK_OUT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_ACCOMMODATION_CHECK_OUT_REQUEST)
                .keyboard(ReplyKeyboardBuilder.buildSkipAndCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processCheckOutResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var checkOutDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        accommodationService.setCheckOutDate(checkOutDate, chatId);
        return prepareFileRequest(chatId);
    }

    private ResponseDto prepareFileRequest(long chatId) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(AccommodationUtil.State.AWAIT_FILE.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_ACCOMMODATION_FILE_REQUEST))
                .keyboard(ReplyKeyboardBuilder.buildCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        if (nonNull(messageDto.getDocument().getFileId())) {
            accommodationService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
        return prepareFinalResponse(chatId);
    }

    @Override
    protected ResponseDto prepareFinalResponse(long chatId) {
        var accommodation = accommodationService.commitNewAccommodation(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_ACCOMMODATION_FINAL_RESPONSE))
                .keyboard(getFinalKeyboard(accommodation.getTripId()))
                .build();
    }

    @Override
    protected ResponseDto processSkip(long chatId) {
        var state = getUserStateManager().getState(chatId);
        return switch (AccommodationUtil.State.valueOf(state.getState())) {
            case AWAIT_ADDRESS -> prepareCheckInRequest(chatId);
            case AWAIT_CHECK_IN -> prepareCheckOutRequest(chatId);
            case AWAIT_CHECK_OUT -> prepareFileRequest(chatId);
            case AWAIT_FILE, AWAIT_TYPE, AWAIT_NAME -> returnErrorMessage();
        };
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_ACCOMMODATION;
    }

}
