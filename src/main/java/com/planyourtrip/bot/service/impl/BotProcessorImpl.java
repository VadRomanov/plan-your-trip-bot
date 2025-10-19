package com.planyourtrip.bot.service.impl;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.service.BotProcessor;
import com.planyourtrip.bot.service.SendService;
import com.planyourtrip.bot.service.command.EventHandler;
import com.planyourtrip.bot.dto.CommandDto;
import com.planyourtrip.bot.dto.ResponseDto;
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
                log.error("Message was not recognized. Will be processed by default {}", update.getUpdateId());
                response = processDefaultCommand(update);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing {}. Error {}", update, e.getMessage(), e);
            response = processErrorCommand(update);
        }

        sendService.sendResponse(response);
        log.debug("End process {}", update);
    }

    private ResponseDto processMessage(Message message) {
        log.info("Message was recognized as message {} chatId {}", message.getMessageId(), message.getChatId());
        if (message.isCommand()) {
            userStateManager.clearState(message.getChatId());
            return eventHandler.handleEvent(telegramMapper.toCommandDto(message));
        } else if (message.hasText() || message.hasDocument()) {
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
        var callbackDto = telegramMapper.toCallbackDto(callback);

        return eventHandler.handleEvent(callbackDto);
    }

    private ResponseDto processDefaultCommand(Update update) {
        CommandDto defaultCommand;
        if (update.hasCallbackQuery()) {
            defaultCommand = telegramMapper.toDefaultCommandDto(update.getCallbackQuery());
        } else {
            defaultCommand = telegramMapper.toDefaultCommandDto(update.getMessage());
        }

        return eventHandler.handleEvent(defaultCommand);
    }

    private ResponseDto processErrorCommand(Update update) {
        var result = processDefaultCommand(update);
        return result.setText(BotAnswer.SOMETHING_WRONG);
    }
}
