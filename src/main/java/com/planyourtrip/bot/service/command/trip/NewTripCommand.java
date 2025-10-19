package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.CommandDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.UserService;
import com.planyourtrip.bot.service.command.impl.AbstractAddCommand;
import com.planyourtrip.bot.service.command.trip.util.TripUtil;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewTripCommand extends AbstractAddCommand {

    private final TripService tripService;
    private final UserService userService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processInitResponse(commandDto.getChatId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return processInitResponse(callbackDto.getChatId());
        } else if (step == 2) {
            return processSkipOrCompleteResponse(callbackDto.getChatId(), callbackDto.getCallbackData().get(1));
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (TripUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_START_DT -> processStartDateResponse(messageDto);
            case AWAIT_END_DT -> processEndDateResponse(messageDto);
        };
    }

    private ResponseDto processInitResponse(long chatId) {
        getUserStateManager().setState(chatId,
                new UserState(getCommandType(), TripUtil.State.AWAIT_NAME.name(), null));
        return ResponseDto.builder()
                .text(format(BotAnswer.NEW_TRIP_NAME_REQUEST, LocalDateTime.now().getYear()))
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        var user = userService.getUserByTelegramId(messageDto.getTelegramId());
        long chatId = messageDto.getChatId();
        tripService.createTrip(messageDto.getMsgText(), user.getId(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TripUtil.State.AWAIT_START_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_TRIP_START_DT_REQUEST)
                .keyboard(ReplyKeyboardBuilder.buildSkipAndCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processStartDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var startDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        tripService.setStartDate(startDate, chatId);
        return prepareEndDateRequest(chatId);
    }

    private ResponseDto prepareEndDateRequest(long chatId) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TripUtil.State.AWAIT_END_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_TRIP_END_DT_REQUEST)
                .keyboard(ReplyKeyboardBuilder.buildCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processEndDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var endDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        tripService.setEndDate(endDate, chatId);
        return prepareFinalResponse(chatId);
    }

    @Override
    protected List<ReplyKeyboardBuilder.KeyboardButton> getFinalKeyboard(long tripId) {
        var buttons = ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.ADD_TICKET,
                CommandType.ADD_ACCOMMODATION, CommandType.ADD_NOTE, CommandType.EDIT_TRIP);
        buttons.add(new ReplyKeyboardBuilder.KeyboardButton(
                CommandType.MY_TRIPS.getDescription(),
                CommandType.MY_TRIPS.getName())
        );
        return buttons;
    }

    @Override
    protected ResponseDto prepareFinalResponse(long chatId) {
        var trip = tripService.commitNewTrip(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(format(BotAnswer.NEW_TRIP_FINAL_RESPONSE, trip.getName()))
                .keyboard(getFinalKeyboard(trip.getId()))
                .build();
    }

    @Override
    protected ResponseDto processSkip(long chatId) {
        var state = getUserStateManager().getState(chatId);
        return switch (TripUtil.State.valueOf(state.getState())) {
            case AWAIT_START_DT -> prepareEndDateRequest(chatId);
            case AWAIT_NAME, AWAIT_END_DT -> returnErrorMessage();
        };
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.NEW_TRIP;
    }

}
