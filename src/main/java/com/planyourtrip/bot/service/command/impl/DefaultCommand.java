package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CommandDto;
import com.planyourtrip.bot.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultCommand extends AbstractCommand {

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return ResponseDto.builder()
                .text(BotAnswer.DEFAULT_ANSWER)
                .keyboard(defaultKeyboard())
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DEFAULT;
    }
}
