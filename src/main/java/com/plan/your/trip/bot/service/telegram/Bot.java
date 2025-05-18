package com.plan.your.trip.bot.service.telegram;

import com.plan.your.trip.bot.properties.BotProperties;
import com.plan.your.trip.bot.service.telegram.commands.CallbackQueryHandler;
import com.plan.your.trip.bot.service.telegram.commands.CommandsHandler;
import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import com.plan.your.trip.bot.service.telegram.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final BotProperties botProperties;
    private final CommandsHandler commandsHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final UserStateManager userStateManager;
    private final Mapper mapper;
    private final TelegramClient telegramClient;

    @Override
    public void consume(Update update) {
        log.debug("Start process {}", update);
        SendMessage response;
        if (update.hasMessage()) {
            response = processMessage(update);
        } else if (update.hasCallbackQuery()) {
            response = callbackQueryHandler.handleCallback(mapper.toCallbackDto(update));
        } else {
            response = commandsHandler.handleCommands(mapper.toDefaultCommandDto(update));
        }
        log.debug("End process {}", update);

        sendResponse(response);
    }

    private SendMessage processMessage(Update update) {
        MessageDto messageDto;
        if (update.getMessage().isCommand()) {
            messageDto = mapper.toCommandDto(update);
        } else if (update.getMessage().hasText()) {
            var state = userStateManager.getState(update.getMessage().getChatId());
            if (nonNull(state)) {
                messageDto = mapper.responseToCommandDto(update, state);
            } else {
                messageDto = mapper.toDefaultCommandDto(update);
            }
        } else {
            messageDto = mapper.toDefaultCommandDto(update);
        }
        return commandsHandler.handleCommands(messageDto);
    }

    private void sendResponse(SendMessage message) {
        try {
            log.debug("Send response {}", message);
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}
