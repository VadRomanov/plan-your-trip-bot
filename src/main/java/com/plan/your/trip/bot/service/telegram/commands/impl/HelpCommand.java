package com.plan.your.trip.bot.service.telegram.commands.impl;

import com.plan.your.trip.bot.service.telegram.dto.CallbackDto;
import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RequiredArgsConstructor
@Component
public class HelpCommand extends CommandProcessor {
    public static final String COMMAND_NAME = "help";

    @Override
    public SendMessage process(MessageDto messageDto) {
        return prepareResponse(messageDto.chatId());
    }

    @Override
    public SendMessage processCallback(CallbackDto callbackDto) {
        return prepareResponse(callbackDto.chatId());
    }

    private SendMessage prepareResponse(long chatId) {
        return SendMessage.builder()
                .text("""
                        Я помогу тебе создать и управлять твоими путешествиями ✈️.
                        Ты можешь управлять мною с помощью следующих команд:
                        /newtrip - создать новое путешествие
                        /mytrips - управлять своими путешествиями
                        /deletetrip - удалить путешествие""")
                .chatId(chatId)
                .build();
    }

    @Override
    public String getProcessorName() {
        return COMMAND_NAME;
    }
}
