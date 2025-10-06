package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.CommandDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.UserService;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
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
public class NewTripCommand extends AbstractCommand {

    private final TripService tripService;
    private final UserService userService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processInitResponse(commandDto.getChatId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return processInitResponse(callbackDto.getChatId());
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (TripUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_NAME -> processNameResponse(messageDto);
            case AWAIT_START_DT -> processStartDtResponse(messageDto);
            case AWAIT_END_DT -> processEndDtResponse(messageDto);
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
                .build();
    }

    private ResponseDto processStartDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var startDt = DateTimeUtils.parseDate(messageDto.getMsgText());
        tripService.setStartDt(startDt, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TripUtil.State.AWAIT_END_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_TRIP_END_DT_REQUEST)
                .build();
    }

    private ResponseDto processEndDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var endDt = DateTimeUtils.parseDate(messageDto.getMsgText());
        tripService.setEndDt(endDt, chatId);
        var trip = tripService.commitNewTrip(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(format(BotAnswer.NEW_TRIP_FINAL_RESPONSE, trip.getName()))
                .keyboard(getFinalKeyboard(trip.getId()))
                .build();
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
    public CommandType getCommandType() {
        return CommandType.NEW_TRIP;
    }

}
