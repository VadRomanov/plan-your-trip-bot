package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractEditCommand;
import com.planyourtrip.bot.service.command.trip.util.TripUtil;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditTripCommand extends AbstractEditCommand {
    private final TripService tripService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return processEntityIdResponse(callbackDto);
        } else if (step == 3) {
            return requestNewValue(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (TripUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_START_DT -> processStartDtResponse(messageDto);
            case AWAIT_END_DT -> processEndDtResponse(messageDto);
        };
    }

    @Override
    protected ResponseDto processEntityIdResponse(CallbackDto callbackDto) {
        long tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        return ResponseDto.builder()
                .text(BotAnswer.EDIT_CHOOSE_FIELD_REQUEST)
                .keyboard(createStateKeyboard(tripId))
                .build();
    }

    @Override
    protected ResponseDto requestNewValue(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        long tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var newValue = TripUtil.State.valueOf(callbackDto.getCallbackData().get(2));
        tripService.fetchTripById(tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(newValue.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_REQUEST)
                .build();
    }

    protected List<ReplyKeyboardBuilder.KeyboardButton> createStateKeyboard(long tripId) {
        var buttons = new ArrayList<ReplyKeyboardBuilder.KeyboardButton>();
        for (var state : TripUtil.State.values()) {
            buttons.add(new ReplyKeyboardBuilder.KeyboardButton(state.getValue(),
                    String.format("%s/%s/%s", getCommandType().getName(), tripId,
                            state)));
        }
        buttons.addAll(ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.EDIT_TICKET,
                CommandType.EDIT_ACCOMMODATION, CommandType.EDIT_NOTE));
        return buttons;
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var trip = tripService.getTripToUpdate(chatId);
        trip.setName(messageDto.getMsgText());
        tripService.updateTrip(trip, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processStartDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var trip = tripService.getTripToUpdate(chatId);
        var startDt = DateTimeUtils.parseDate(messageDto.getMsgText());
        trip.setStartDate(startDt);
        tripService.updateTrip(trip, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processEndDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var trip = tripService.getTripToUpdate(chatId);
        var endDt = DateTimeUtils.parseDate(messageDto.getMsgText());
        trip.setEndDate(endDt);
        tripService.updateTrip(trip, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    @Override
    protected ResponseDto requestEntityId(long tripId) {
        return null;// not implemented
    }

    @Override
    protected List<ReplyKeyboardBuilder.KeyboardButton> createStatesKeyboard(long tripId, long id) {
        return null;// not implemented
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_TRIP;
    }

}
