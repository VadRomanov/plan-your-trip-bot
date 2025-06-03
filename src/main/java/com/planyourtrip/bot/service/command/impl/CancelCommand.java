package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CancelCommand extends AbstractCommand {

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processCancel(commandDto.getChatId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return processCancel(callbackDto.getChatId());
    }

    private ResponseDto processCancel(long chatId) {
        var state = getReplyKeyboardHistory().restoreState(chatId);
        return ResponseDto.builder()
                .text(state.getText())
                .keyboard(state.getKeyboard())
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.CANCEL;
    }
}
