package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.impl.TripServiceImpl;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.planyourtrip.bot.constant.BotAnswer.CANCEL;
import static com.planyourtrip.bot.constant.BotAnswer.CONFIRMED;
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
        return processInitResponse(commandDto.getTelegramId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().length;
        if (step == 1) {
            return processInitResponse(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestConfirmation(callbackDto);
        } else if (step == 3) {
            return processConfirmation(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto processInitResponse(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(DELETE_INIT_RESPONSE)
                .keyboard(ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.DELETE_TRIP))
                .build();
    }

    private ResponseDto requestConfirmation(CallbackDto callbackDto) {
        var trip = tripService.getTripById(Long.parseLong(callbackDto.getCallbackData()[1]));
        return ResponseDto.builder()
                .text(String.format(DELETE_CONFIRMATION_REQUEST, trip.getName(),
                        trip.getExpired() ? EXPIRED : Strings.EMPTY))
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(
                                DELETE_CONFIRMATION_BUTTON,
                                String.format("%s/%s/%s", CommandType.DELETE_TRIP.getName(), trip.getId(),
                                        CONFIRMED)),
                        new ReplyKeyboardBuilder.KeyboardButton(CANCEL, CommandType.CANCEL.getName())))
                .build();
    }

    private ResponseDto processConfirmation(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData()[2].equals(CONFIRMED)) {
            return doDelete(callbackDto);
        } else {
            return processInitResponse(callbackDto.getTelegramId());
        }
    }

    private ResponseDto doDelete(CallbackDto callbackDto) {
        tripService.deleteTrip(Long.parseLong(callbackDto.getCallbackData()[1]));
        return ResponseDto.builder()
                .text(DELETE_FINAL_RESPONSE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_TRIP;
    }
}
