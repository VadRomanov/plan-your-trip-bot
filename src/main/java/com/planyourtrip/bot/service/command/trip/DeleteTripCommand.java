package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractDeleteCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteTripCommand extends AbstractDeleteCommand {
    private final TripService tripService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestConfirmation(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else if (step == 3) {
            return processConfirmation(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    protected ResponseDto requestConfirmation(long id) {
        var trip = tripService.getTripById(id);
        var text = String.format(BotAnswer.DELETE_TRIP_CONFIRMATION_REQUEST, trip.getName(),
                trip.isExpired() ? BotAnswer.EXPIRED : Strings.EMPTY);
        return requestConfirmation(text, id);
    }

    @Override
    protected ResponseDto processConfirmation(CallbackDto callbackDto) {
        if (callbackDto.getCallbackData().get(2).equals(BotAnswer.CONFIRMED)) {
            return doDelete(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    protected ResponseDto doDelete(long id) {
        tripService.deleteTrip(id);
        return ResponseDto.builder()
                .text(BotAnswer.DELETE_TRIP_FINAL_RESPONSE)
                .build();
    }

    @Override
    protected ResponseDto requestEntityId(long tripId) {
        return null; // not implemented
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_TRIP;
    }
}
