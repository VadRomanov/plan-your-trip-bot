package com.plan.your.trip.bot.service.telegram.commands.impl;

import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@RequiredArgsConstructor
@Component
public class StartCommand extends CommandProcessor {
    public static final String COMMAND_NAME = "start";

    @Override
    public SendMessage process(MessageDto messageDto) {
        return SendMessage.builder()
                .text("""
                        \uD83C\uDF0D Добро пожаловать! Я помогу вам планировать путешествия.
                        
                        Используйте /newtrip для создания нового путешествия,
                        /mytrips для просмотра и изменения ваших путешествий,
                        /help для просмотра доступных команд.""")
                .chatId(messageDto.chatId())
                .replyMarkup(getKeyboard())
                .build();
    }

    private ReplyKeyboard getKeyboard() {
        var newTripButton = InlineKeyboardButton.builder()
                .text("Создать новое путешествие")
                .callbackData("newtrip")
                .build();
        var myTripsButton = InlineKeyboardButton.builder()
                .text("Мои путешествия")
                .callbackData("mytrips")
                .build();
        var helpButton = InlineKeyboardButton.builder()
                .text("Команды")
                .callbackData("help")
                .build();

        var row1 = new InlineKeyboardRow();
        row1.add(newTripButton);
        row1.add(myTripsButton);
        var row2 = new InlineKeyboardRow();
        row2.add(helpButton);

        var rows = List.of(row1, row2);

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    @Override
    public String getProcessorName() {
        return COMMAND_NAME;
    }
}
