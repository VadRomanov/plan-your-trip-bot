package com.planyourtrip.bot.service.impl;

import com.planyourtrip.bot.service.BotProcessor;
import com.planyourtrip.bot.service.SendService;
import com.planyourtrip.bot.service.command.EventHandler;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.mapper.TelegramMapper;
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
    private final TelegramMapper telegramMapper;

    @Override
    public void process(Update update) {
        log.debug("Start process {}", update);
        ResponseDto response;
        try {
            if (update.hasMessage()) {
                response = processMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                response = processCallback(update.getCallbackQuery());
            } else {
                response = processDefaultCommand(update);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing {}. Error {}", update, e.getMessage(), e);
            response = processDefaultCommand(update);
        }

        sendService.sendResponse(response);
        log.debug("End process {}", update);
    }

    private ResponseDto processMessage(Message message) {
        log.info("Message was recognized as message {} chatId {}", message.getMessageId(), message.getChatId());
        if (message.isCommand()) {
            userStateManager.clearState(message.getChatId());
            return eventHandler.handleEvent(telegramMapper.toCommandDto(message));
        } else if (message.hasText()) {
            var state = userStateManager.getState(message.getChatId());
            if (nonNull(state)) {
                return eventHandler.handleEvent(telegramMapper.toResponseMessageDto(message, state));
            } else {
                return eventHandler.handleEvent(telegramMapper.toDefaultCommandDto(message));
            }
        } else {
            return eventHandler.handleEvent(telegramMapper.toDefaultCommandDto(message));
        }
    }

    private ResponseDto processCallback(CallbackQuery callback) {
        log.info("Message was recognized as callback {} chatId {}", callback.getId(),
                callback.getMessage().getChatId());
        userStateManager.clearState(callback.getMessage().getChatId());
        var callbackDto = telegramMapper.toCallbackDto(callback);

        return eventHandler.handleEvent(callbackDto);
    }

    private ResponseDto processDefaultCommand(Update update) {
        log.info("Message was not recognized. will be processed by default {}", update.getUpdateId());
        var defaultCommand = telegramMapper.toDefaultCommandDto(update);

        return eventHandler.handleEvent(defaultCommand);
    }
}
