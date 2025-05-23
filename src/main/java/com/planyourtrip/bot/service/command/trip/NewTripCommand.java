package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.service.command.UserService;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.mapper.Mapper;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewTripCommand extends AbstractCommand {

    private final TripService tripService;
    private final UserService userService;
    private final Mapper mapper;

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
        try {
            return switch (State.valueOf(messageDto.getState().getState())) {
                case AWAIT_NAME -> processNameResponse(messageDto);
                case AWAIT_START_DT -> processStartDtResponse(messageDto);
                case AWAIT_END_DT -> processEndDtResponse(messageDto);
            };
        } catch (BusinessException e) {
            log.error("Error while process message command {}", CommandType.NEW_TRIP.getName(), e);
            return returnErrorMessage(messageDto.getChatId(), messageDto.getMessageId());
        }
    }

    private ResponseDto processInitResponse(long chatId) {
        getUserStateManager().setState(chatId,
                new UserState(CommandType.NEW_TRIP, State.AWAIT_NAME.name(), null));
        return ResponseDto.builder()
                .text(format(BotAnswer.NEW_TRIP_INIT_RESPONSE, LocalDateTime.now().getYear()))
                .chatId(chatId)
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        var user = userService.createOrUpdateUser(mapper.messageToUserDto(messageDto));
        long chatId = messageDto.getChatId();
        tripService.createTrip(messageDto.getMsgText(), user.getId(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.NEW_TRIP)
                .setState(State.AWAIT_START_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_TRIP_NAME_RESPONSE)
                .chatId(chatId)
                .replyToMessageId(messageDto.getMessageId())
                .build();
    }

    private ResponseDto processStartDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var startDt = DateUtils.parseDate(messageDto.getMsgText());
        tripService.setStartDt(startDt, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.NEW_TRIP)
                .setState(State.AWAIT_END_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_TRIP_START_DT_RESPONSE)
                .chatId(chatId)
                .replyToMessageId(messageDto.getMessageId())
                .build();
    }

    private ResponseDto processEndDtResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var endDt = DateUtils.parseDate(messageDto.getMsgText());
        tripService.setEndDt(endDt, chatId);
        var trip = tripService.commitNewTrip(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(format(BotAnswer.NEW_TRIP_FINAL_RESPONSE, trip.getName()))
                .chatId(chatId)
                .replyToMessageId(messageDto.getMessageId())
                .keyboard(getFinalKeyboard(trip.getId()))
                .build();
    }

    private ReplyKeyboard getFinalKeyboard(long tripId) {
        return ReplyKeyboardBuilder.buildInlineKeyboard(List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.ADD_TICKET.getDescription(),
                        format("%s/%s", CommandType.ADD_TICKET.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.ADD_HOTEL.getDescription(),
                        format("%s/%s", CommandType.ADD_HOTEL.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.ADD_NOTE.getDescription(),
                        format("%s/%s", CommandType.ADD_NOTE.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_TRIP.getDescription(),
                        format("%s/%s", CommandType.EDIT_TRIP.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.MY_TRIPS.getDescription(),
                        CommandType.MY_TRIPS.getName())
        ));
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.NEW_TRIP;
    }

    private enum State {
        AWAIT_NAME, AWAIT_START_DT, AWAIT_END_DT
    }
}
