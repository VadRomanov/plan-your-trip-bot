package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractEditCommand extends AbstractCommand {

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestEntityId(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else if (step == 3) {
            return processEntityIdResponse(callbackDto);
        } else if (step == 4) {
            return requestNewValue(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    protected ResponseDto processEntityIdResponse(CallbackDto callbackDto) {
        long tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        long entityId = Long.parseLong(callbackDto.getCallbackData().get(2));
        return ResponseDto.builder()
                .text(BotAnswer.EDIT_CHOOSE_FIELD_REQUEST)
                .keyboard(createStatesKeyboard(tripId, entityId))
                .build();
    }

    protected abstract ResponseDto requestEntityId(long tripId);

    protected abstract ResponseDto requestNewValue(CallbackDto callbackDto);

    protected abstract List<ReplyKeyboardBuilder.KeyboardButton> createStatesKeyboard(long tripId, long id);
}
