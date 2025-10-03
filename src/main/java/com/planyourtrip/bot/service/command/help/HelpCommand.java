package com.planyourtrip.bot.service.command.help;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.CommandDto;
import com.planyourtrip.bot.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HelpCommand extends AbstractCommand {
    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return prepareResponse();
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return prepareResponse();
    }

    private ResponseDto prepareResponse() {
        return ResponseDto.builder()
                .text(BotAnswer.HELP_ANSWER)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.HELP;
    }
}
