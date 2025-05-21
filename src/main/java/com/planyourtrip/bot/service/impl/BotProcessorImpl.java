package com.planyourtrip.bot.service.impl;

import com.planyourtrip.bot.service.BotProcessor;
import com.planyourtrip.bot.service.SendService;
import com.planyourtrip.bot.service.command.EventHandler;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.mapper.Mapper;
import com.planyourtrip.bot.service.state.UserStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotProcessorImpl implements BotProcessor {
    private final EventHandler eventHandler;
    private final UserStateManager userStateManager;
    private final SendService sendService;
    private final Mapper mapper;

    @Override
    public void process(Update update) {
        log.debug("Start process {}", update);
        ResponseDto response;
        if (update.hasMessage()) {
            response = processMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            response = processCallback(update.getCallbackQuery());
        } else {
            response = processDefaultCommand(update);
        }

        sendService.sendResponse(response);
        log.debug("End process {}", update);
    }

    private ResponseDto processMessage(Message message) {
        log.info("Message was recognized as message {} chatId {}", message.getMessageId(), message.getChatId());
        if (message.isCommand()) {
            userStateManager.clearState(message.getChatId());
            return eventHandler.handleEvent(mapper.toCommandDto(message));
        } else if (message.hasText()) {
            var state = userStateManager.getState(message.getChatId());
            if (nonNull(state)) {
                return eventHandler.handleEvent(mapper.toResponseMessageDto(message, state));
            } else {
                return eventHandler.handleEvent(mapper.toDefaultCommandDto(message));
            }
        } else {
            return eventHandler.handleEvent(mapper.toDefaultCommandDto(message));
        }
    }

    private ResponseDto processCallback(CallbackQuery callback) {
        log.info("Message was recognized as callback {} chatId {}", callback.getId(),
                callback.getMessage().getChatId());
        userStateManager.clearState(callback.getMessage().getChatId());
        var callbackDto = mapper.toCallbackDto(callback);

        return eventHandler.handleEvent(callbackDto);
    }

    private ResponseDto processDefaultCommand(Update update) {
        log.info("Message was not recognized. will be processed by default {}", update.getUpdateId());
        var defaultCommand = mapper.toDefaultCommandDto(update);

        return eventHandler.handleEvent(defaultCommand);
    }
}
