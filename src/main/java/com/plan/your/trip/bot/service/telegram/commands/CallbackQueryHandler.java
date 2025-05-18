package com.plan.your.trip.bot.service.telegram.commands;

import com.plan.your.trip.bot.service.telegram.commands.registry.CommandProcessorRegistry;
import com.plan.your.trip.bot.service.telegram.dto.CallbackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Slf4j
@RequiredArgsConstructor
public class CallbackQueryHandler {

    private final CommandProcessorRegistry registry;

    public SendMessage handleCallback(CallbackDto callbackDto) {
        var commandProcessor = registry.getProcessor(callbackDto.callbackData()[0]);
        return commandProcessor.processCallback(callbackDto);
    }

}
