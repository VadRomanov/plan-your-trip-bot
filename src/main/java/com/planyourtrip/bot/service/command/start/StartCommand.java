package com.planyourtrip.bot.service.command.start;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.dto.CommandDto;
import com.planyourtrip.bot.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;


@RequiredArgsConstructor
@Component
public class StartCommand extends AbstractCommand {
    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return ResponseDto.builder()
                .text(String.format(BotAnswer.START_ANSWER,
                        Objects.requireNonNullElse(commandDto.getFirstName(), commandDto.getUserName())))
                .keyboard(defaultKeyboard())
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.START;
    }
}
