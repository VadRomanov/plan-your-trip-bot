package com.planyourtrip.bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotProcessor {
    void process(Update update);
}
