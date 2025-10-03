package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.lang.String.format;

@Slf4j
public abstract class AbstractMyCommand extends AbstractCommand {

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return prepareAnswer(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else {
            return returnErrorMessage();
        }
    }

    protected List<ReplyKeyboardBuilder.KeyboardButton> getActionsKeyboard(long tripId, CommandType editCommand,
                                                                           CommandType deleteCommand) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(editCommand.getDescription(),
                        String.format("%s/%s", editCommand.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(deleteCommand.getDescription(),
                        String.format("%s/%s", deleteCommand.getName(), tripId))
        );

    }

    protected List<ReplyKeyboardBuilder.KeyboardButton> getAddKeyboard(long tripId, CommandType addCommand) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(addCommand.getDescription(),
                        format("%s/%s", addCommand.getName(), tripId))
        );
    }

    protected abstract ResponseDto prepareAnswer(long id);

}