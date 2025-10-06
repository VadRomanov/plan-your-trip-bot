package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;

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

    protected abstract ResponseDto prepareAnswer(long id);

}