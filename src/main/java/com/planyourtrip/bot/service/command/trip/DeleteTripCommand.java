package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.TripDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.impl.TripServiceImpl;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

import static com.planyourtrip.bot.constant.BotAnswer.DELETE_CONFIRMATION_BUTTON;
import static com.planyourtrip.bot.constant.BotAnswer.DELETE_CONFIRMATION_REQUEST;
import static com.planyourtrip.bot.constant.BotAnswer.DELETE_FINAL_RESPONSE;
import static com.planyourtrip.bot.constant.BotAnswer.DELETE_INIT_RESPONSE;
import static com.planyourtrip.bot.constant.BotAnswer.EXPIRED;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteTripCommand extends AbstractCommand {
    private final TripServiceImpl tripService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processInitResponse(commandDto.getUserId(), commandDto.getChatId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData().length == 1) {
            return processInitResponse(callbackDto.getUserId(), callbackDto.getChatId());
        } else {
            if (callbackDto.getCallbackData().length == 3) {
                tripService.deleteTrip(Long.parseLong(callbackDto.getCallbackData()[1]));
                return ResponseDto.builder()
                        .text(DELETE_FINAL_RESPONSE)
                        .chatId(callbackDto.getChatId())
                        .build();
            }
            var trip = tripService.getTripById(Long.parseLong(callbackDto.getCallbackData()[1]));
            return ResponseDto.builder()
                    .text(String.format(DELETE_CONFIRMATION_REQUEST, trip.getName(),
                            trip.getExpired() ? EXPIRED : Strings.EMPTY))
                    .chatId(callbackDto.getChatId())
                    .keyboard(getConfirmKeyboard(trip))
                    .build();
        }
    }

    private ResponseDto processInitResponse(long userId, long chatId) {
        var trips = tripService.getTripsByUserId(userId);

        return ResponseDto.builder()
                .text(DELETE_INIT_RESPONSE)
                .chatId(chatId)
                .keyboard(ReplyKeyboardBuilder.buildTripsInlineKeyboard(trips, CommandType.DELETE_TRIP))
                .build();
    }

    private ReplyKeyboard getConfirmKeyboard(TripDto trip) {
        var row = new InlineKeyboardRow();
        var newTripButton = InlineKeyboardButton.builder()
                .text(DELETE_CONFIRMATION_BUTTON)
                .callbackData(
                        String.format("%s/%s/%s", CommandType.DELETE_TRIP.getName(), trip.getId(), "confirmed"))
                .build();
        row.add(newTripButton);
        var rows = List.of(row);
        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_TRIP;
    }
}
