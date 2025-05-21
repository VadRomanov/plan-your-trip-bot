package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.service.command.registry.Command;
import com.planyourtrip.bot.service.command.registry.CommandProcessorRegistry;
import com.planyourtrip.bot.service.dto.AbstractRequestDto;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.state.impl.UserStateManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractCommand implements Command {

    @Autowired
    private CommandProcessorRegistry commandProcessorRegistry;
    @Autowired
    private UserStateManagerImpl userStateManager;

    @Override
    public void afterPropertiesSet() {
        commandProcessorRegistry.register(getCommandType(), this);
    }

    @Override
    public final ResponseDto process(AbstractRequestDto requestDto) {
        return switch (requestDto) {
            case CommandDto commandDto -> processCommand(commandDto);
            case CallbackDto callbackDto -> processCallback(callbackDto);
            case MessageDto messageDto -> processMessage(messageDto);
            default -> throw new IllegalStateException("Unexpected value: " + requestDto);
        };
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return null;
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return null;
    }

    protected UserStateManagerImpl getUserStateManager() {
        return userStateManager;
    }
}
