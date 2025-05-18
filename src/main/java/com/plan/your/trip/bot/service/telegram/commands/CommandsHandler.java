package com.plan.your.trip.bot.service.telegram.commands;

import com.plan.your.trip.bot.service.telegram.commands.registry.CommandProcessorRegistry;
import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandsHandler {

    private final CommandProcessorRegistry registry;

    public SendMessage handleCommands(MessageDto messageDto) {
        var commandProcessor = registry.getProcessor(messageDto.commandName());
        log.info("Start process command {}. MessageId={}", commandProcessor.getProcessorName(),
                messageDto.messageId());
        var result = commandProcessor.process(messageDto);

        log.info("End process command {}. MessageId={}", commandProcessor.getProcessorName(),
                messageDto.messageId());
        return result;
    }

}
