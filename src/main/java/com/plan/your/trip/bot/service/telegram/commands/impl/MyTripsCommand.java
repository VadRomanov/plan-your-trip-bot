package com.plan.your.trip.bot.service.telegram.commands.impl;

import com.plan.your.trip.bot.model.Trip;
import com.plan.your.trip.bot.service.TripService;
import com.plan.your.trip.bot.service.telegram.dto.CallbackDto;
import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyTripsCommand extends CommandProcessor {
    public static final String COMMAND_NAME = "mytrips";
    private static final String RESPONSE = "Список ваших путешествий:";

    private final TripService tripService;

    @Override
    public SendMessage process(MessageDto messageDto) {
        return processInitResponse(messageDto.userId(), messageDto.chatId());
    }

    @Override
    public SendMessage processCallback(CallbackDto callbackDto) {
        if (callbackDto.callbackData().length == 1) {
            return processInitResponse(callbackDto.userId(), callbackDto.chatId());
        } else {
            var trip = tripService.getTrip(Long.parseLong(callbackDto.callbackData()[1]));
            return SendMessage.builder()
                    .text(String.format("%s%s", trip.getName(), trip.getExpired() ? " (Завершено)" : ""))
                    .chatId(callbackDto.chatId())
                    .replyMarkup(getActionsKeyboard(trip))
                    .build();
        }
    }

    private SendMessage processInitResponse(long userId, long chatId) {
        var trips = tripService.getTripsByUserId(userId);

        return SendMessage.builder()
                .text(RESPONSE)
                .chatId(chatId)
                .replyMarkup(getTripsKeyboard(trips))
                .build();
    }

    private ReplyKeyboard getActionsKeyboard(Trip trip) {
        var row = new InlineKeyboardRow();
        var edit = InlineKeyboardButton.builder()
                .text("Изменить")
                .callbackData(String.format("%s_%s", DefaultCommand.COMMAND_NAME, trip.getId()))
                .build();
        var delete = InlineKeyboardButton.builder()
                .text("Удалить")
                .callbackData(String.format("%s_%s", DeleteTripCommand.COMMAND_NAME, trip.getId()))
                .build();
        row.add(edit);
        row.add(delete);


        var rows = List.of(row);

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    private ReplyKeyboard getTripsKeyboard(List<Trip> trips) {
        var row = new InlineKeyboardRow();
        for (var trip : trips) {
            var newTripButton = InlineKeyboardButton.builder()
                    .text(trip.getName())
                    .callbackData(String.format("%s_%s", COMMAND_NAME, trip.getId()))
                    .build();
            row.add(newTripButton);
        }
        var rows = List.of(row);

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    @Override
    public String getProcessorName() {
        return COMMAND_NAME;
    }
}
