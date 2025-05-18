package com.plan.your.trip.bot.config;

import com.plan.your.trip.bot.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@EnableConfigurationProperties(BotProperties.class)
@RequiredArgsConstructor
public class AppConfig {
    private final BotProperties botProperties;

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(botProperties.getToken());
    }
}