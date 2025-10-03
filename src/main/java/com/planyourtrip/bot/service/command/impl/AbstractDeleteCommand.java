package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractDeleteCommand extends AbstractCommand {

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestEntityId(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else if (step == 3) {
            return requestConfirmation(Long.parseLong(callbackDto.getCallbackData().get(2)));
        } else if (step == 4) {
            return processConfirmation(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    protected ResponseDto requestConfirmation(String text, long tripId, long id) {
        return ResponseDto.builder()
                .text(text)
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(
                                BotAnswer.DELETE_CONFIRMATION_REQUEST,
                                String.format("%s/%s/%s/%s", getCommandType().getName(), tripId, id,
                                        BotAnswer.CONFIRMED)),
                        new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.CANCEL, CommandType.CANCEL.getName())))
                .build();
    }

    protected ResponseDto requestConfirmation(String text, long tripId) {
        return ResponseDto.builder()
                .text(text)
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(
                                BotAnswer.DELETE_CONFIRMATION_REQUEST,
                                String.format("%s/%s/%s", getCommandType().getName(), tripId,
                                        BotAnswer.CONFIRMED)),
                        new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.CANCEL, CommandType.CANCEL.getName())))
                .build();
    }

    protected ResponseDto processConfirmation(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData().get(3).equals(BotAnswer.CONFIRMED)) {
            return doDelete(Long.parseLong(callbackDto.getCallbackData().get(2)));
        } else {
            return returnErrorMessage();
        }
    }

    protected abstract ResponseDto doDelete(long id);

    protected abstract ResponseDto requestEntityId(long tripId);

    protected abstract ResponseDto requestConfirmation(long id);

}
