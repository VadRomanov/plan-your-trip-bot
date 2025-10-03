package com.planyourtrip.bot.service.command.accommodation;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.dto.domain.AccommodationType;
import com.planyourtrip.bot.service.command.accommodation.util.AccommodationUtil;
import com.planyourtrip.bot.service.command.impl.AbstractEditCommand;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditAccommodationCommand extends AbstractEditCommand {
    private final AccommodationService accommodationService;

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (AccommodationUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TYPE -> processTypeResponse(messageDto);
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_CHECK_IN -> processCheckInDateResponse(messageDto);
            case AWAIT_CHECK_OUT -> processCheckOutDateResponse(messageDto);
            case AWAIT_ADDRESS -> processAddressResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
        };
    }

    @Override
    protected ResponseDto requestEntityId(long tripId) {
        var accommodations = accommodationService.getAccommodationsByTripId(tripId);
        return ResponseDto.builder()
                .text(accommodations.isEmpty() ? BotAnswer.MY_ACCOMMODATIONS_EMPTY_RESPONSE :
                        BotAnswer.CHOOSE_ACCOMMODATION_REQUEST)
                .keyboard(accommodations.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_ACCOMMODATION)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(
                        AccommodationUtil.mapAccommodationsToMap(accommodations), tripId,
                        getCommandType()))
                .build();
    }

    @Override
    protected ResponseDto requestNewValue(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var accommodationId = Long.parseLong(callbackDto.getCallbackData().get(2));
        var newValue = AccommodationUtil.State.valueOf(callbackDto.getCallbackData().get(3));
        accommodationService.fetchAccommodationById(accommodationId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(newValue.name()));
        if (newValue.equals(AccommodationUtil.State.AWAIT_TYPE)) {
            return ResponseDto.builder()
                    .text(BotAnswer.ADD_ACCOMMODATION_TYPE_REQUEST)
                    .keyboard(AccommodationUtil.getAccommodationTypesKeyboard(tripId))
                    .build();
        }
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_REQUEST)
                .build();
    }

    @Override
    protected List<ReplyKeyboardBuilder.KeyboardButton> createStatesKeyboard(long tripId, long id) {
        var buttons = new ArrayList<ReplyKeyboardBuilder.KeyboardButton>();
        for (var state : AccommodationUtil.State.values()) {
            buttons.add(new ReplyKeyboardBuilder.KeyboardButton(state.getValue(),
                    String.format("%s/%s/%s/%s", getCommandType().getName(), tripId, id,
                            state)));
        }
        return buttons;
    }

    private ResponseDto processTypeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var accommodation = accommodationService.getAccommodationToUpdate(chatId);
        accommodation.setType(AccommodationType.valueOf(messageDto.getMsgText()));
        accommodationService.updateAccommodation(accommodation, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var accommodation = accommodationService.getAccommodationToUpdate(chatId);
        accommodation.setName(messageDto.getMsgText());
        accommodationService.updateAccommodation(accommodation, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processCheckInDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var accommodation = accommodationService.getAccommodationToUpdate(chatId);
        var checkOutDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        accommodation.setCheckInDate(checkOutDate);
        accommodationService.updateAccommodation(accommodation, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processCheckOutDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var accommodation = accommodationService.getAccommodationToUpdate(chatId);
        var checkOutDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        accommodation.setCheckOutDate(checkOutDate);
        accommodationService.updateAccommodation(accommodation, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processAddressResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var accommodation = accommodationService.getAccommodationToUpdate(chatId);
        accommodation.setAddress(messageDto.getMsgText());
        accommodationService.updateAccommodation(accommodation, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var accommodation = accommodationService.getAccommodationToUpdate(chatId);
        if (nonNull(messageDto.getDocument().getFileId())) {
            accommodationService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
        accommodationService.updateAccommodation(accommodation, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_ACCOMMODATION;
    }

}
