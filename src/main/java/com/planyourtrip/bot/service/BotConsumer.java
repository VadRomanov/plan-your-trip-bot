package com.planyourtrip.bot.service;

import com.planyourtrip.bot.config.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotConsumer implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final BotProperties botProperties;
    private final BotProcessor botProcessor;

    @Override
    public void consume(Update update) {
        log.info("Telegram event received {}", update.getUpdateId());

        botProcessor.process(update);

        log.info("Telegram event processed {}", update.getUpdateId());
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
