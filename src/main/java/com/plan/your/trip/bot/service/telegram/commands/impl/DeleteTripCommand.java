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
public class DeleteTripCommand extends CommandProcessor {
    public static final String COMMAND_NAME = "deletetrip";
    private static final String RESPONSE = "Выберите путешествие, которое хотите удалить:";
    private static final String FINAL_RESPONSE = "Путешествие удалено.";

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
            if (callbackDto.callbackData().length == 2) {
                tripService.deleteTrip(Long.parseLong(callbackDto.callbackData()[1]));
                return SendMessage.builder()
                        .text(String.format(FINAL_RESPONSE))
                        .chatId(callbackDto.chatId())
                        .build();
            }
            var trip = tripService.getTrip(Long.parseLong(callbackDto.callbackData()[1]));

            return SendMessage.builder()
                    .text(String.format("%s%s", trip.getName(), trip.getExpired() ? " (Завершено)" : ""))
                    .chatId(callbackDto.chatId())
                    //      .replyMarkup(getActionsKeyboard(trip))
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

    private ReplyKeyboard getTripsKeyboard(List<Trip> trips) {
        var row = new InlineKeyboardRow();
        for (var trip : trips) {
            var newTripButton = InlineKeyboardButton.builder()
                    .text(String.format("%s_%s", COMMAND_NAME, trip.getName()))
                    .callbackData(trip.getId().toString())
                    .build();
            row.add(newTripButton);
        }
        var rows = List.of(row);

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    private ReplyKeyboard getConfirmKeyboard(List<Trip> trips) {
        var row = new InlineKeyboardRow();
        for (var trip : trips) {
            var newTripButton = InlineKeyboardButton.builder()
                    .text(String.format("%s_%s", COMMAND_NAME, trip.getName()))
                    .callbackData(trip.getId().toString())
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
