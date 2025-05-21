package com.planyourtrip.bot.utils;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.TripDto;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class ReplyKeyboardBuilder {
    private static final int DEFAULT_BUTTON_PER_ROW_COUNT = 2;

    public static ReplyKeyboard buildTripsInlineKeyboard(Collection<TripDto> trips, CommandType commandType) {
        List<ReplyKeyboardBuilder.KeyboardButton> buttons = new ArrayList<>();
        for (var trip : trips) {
            var newTripButton = new ReplyKeyboardBuilder.KeyboardButton(
                    trip.getName(),
                    String.format("%s/%s", commandType.getName(), trip.getId())
            );
            buttons.add(newTripButton);
        }
        return buildInlineKeyboard(buttons);

    }

    public static ReplyKeyboard buildInlineKeyboard(List<KeyboardButton> buttons) {
        return buildInlineKeyboard(buttons, DEFAULT_BUTTON_PER_ROW_COUNT);
    }

    public static ReplyKeyboard buildInlineKeyboard(List<KeyboardButton> buttons, int buttonsPerRow) {

        final List<InlineKeyboardRow> rowList = new ArrayList<>();
        InlineKeyboardRow buttonsRow = null;

        for (int i = 0; i < buttons.size(); i++) {
            if (i % buttonsPerRow == 0) {
                buttonsRow = new InlineKeyboardRow();
            }

            var button = InlineKeyboardButton.builder()
                    .text(buttons.get(i).text())
                    .callbackData(buttons.get(i).callbackData())
                    .build();
            buttonsRow.add(button);

            if (i % buttonsPerRow == 0) {
                rowList.add(buttonsRow);
            }
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();
    }


    public record KeyboardButton(String text, String callbackData) {
    }
}
