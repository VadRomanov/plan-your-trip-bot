package com.plan.your.trip.bot.service.telegram.commands.impl;

import com.plan.your.trip.bot.service.telegram.UserStateManager;
import com.plan.your.trip.bot.service.telegram.commands.Command;
import com.plan.your.trip.bot.service.telegram.commands.registry.CommandProcessorRegistry;
import com.plan.your.trip.bot.service.telegram.dto.CallbackDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
public abstract class CommandProcessor implements Command {

    @Autowired
    private CommandProcessorRegistry commandProcessorRegistry;
    @Autowired
    private UserStateManager userStateManager;

    @Override
    public void afterPropertiesSet() {
        commandProcessorRegistry.register(getProcessorName(), this);
    }

    @Override
    public SendMessage processCallback(CallbackDto callbackDto) {
        return null;
    }

    public abstract String getProcessorName();

    protected UserStateManager getUserStateManager() {
        return userStateManager;
    }
}
