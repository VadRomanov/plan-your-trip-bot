package com.planyourtrip.bot.service.command.help;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HelpCommand extends AbstractCommand {
    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return prepareResponse(commandDto.getChatId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return prepareResponse(callbackDto.getChatId());
    }

    private ResponseDto prepareResponse(long chatId) {
        return ResponseDto.builder()
                .text(BotAnswer.HELP_ANSWER)
                .chatId(chatId)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.HELP;
    }
}
