package com.plan.your.trip.bot.service.telegram.commands.impl;

import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.plan.your.trip.bot.service.telegram.commands.Constants.DEFAULT_COMMAND_PROCESSOR_NAME;

@RequiredArgsConstructor
@Component
public class DefaultCommand extends CommandProcessor {
    public static final String COMMAND_NAME = DEFAULT_COMMAND_PROCESSOR_NAME;
    public static final String DEFAULT_RESPONSE =
            "Команда не распознана. Используйте команду /help для просмотра доступных команд.";

    @Override
    public SendMessage process(MessageDto messageDto) {
        return SendMessage.builder()
                .text(DEFAULT_RESPONSE)
                .chatId(messageDto.chatId())
                .build();
    }

    @Override
    public String getProcessorName() {
        return COMMAND_NAME;
    }
}
