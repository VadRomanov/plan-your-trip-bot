package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractAddCommand extends AbstractCommand {
    protected static final List<String> COMPLETE_SKIP_VALUES = List.of(BotAnswer.COMPLETE, BotAnswer.SKIP);

    protected ResponseDto processSkipOrCompleteResponse(long chatId, String action) {
        if (action.equals(BotAnswer.COMPLETE)) {
            return prepareFinalResponse(chatId);
        } else if (action.equals(BotAnswer.SKIP)) {
            return processSkip(chatId);
        } else {
            return returnErrorMessage();
        }
    }

    protected abstract ResponseDto prepareFinalResponse(long chatId);

    protected abstract ResponseDto processSkip(long chatId);

}
