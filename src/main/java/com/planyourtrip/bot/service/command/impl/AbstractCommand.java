package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.AbstractRequestDto;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.CommandDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.service.command.UserService;
import com.planyourtrip.bot.service.command.registry.Command;
import com.planyourtrip.bot.service.command.registry.CommandProcessorRegistry;
import com.planyourtrip.bot.service.command.trip.TripService;
import com.planyourtrip.bot.service.mapper.Mapper;
import com.planyourtrip.bot.service.state.UserStateManager;
import com.planyourtrip.bot.service.state.callback.ReplyKeyboardHistory;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.planyourtrip.bot.constant.CommandType.HELP;
import static com.planyourtrip.bot.constant.CommandType.MY_TRIPS;
import static com.planyourtrip.bot.constant.CommandType.NEW_TRIP;
import static java.lang.String.format;
import static java.util.Objects.isNull;

@Slf4j
public abstract class AbstractCommand implements Command {

    @Autowired
    private CommandProcessorRegistry commandProcessorRegistry;
    @Autowired
    private UserStateManager userStateManager;
    @Autowired
    private ReplyKeyboardHistory replyKeyboardHistory;
    @Autowired
    private UserService userService;
    @Autowired
    private TripService tripService;
    @Autowired
    private Mapper mapper;

    @Override
    public void afterPropertiesSet() {
        commandProcessorRegistry.register(getCommandType(), this);
    }

    @Override
    public final ResponseDto process(AbstractRequestDto requestDto) {
        try {
            var user = userService.getUserByTelegramId(requestDto.getTelegramId());
            if (isNull(user)) {
                userService.createOrUpdateUser(mapper.requestToUserDto(requestDto));
            }
            return switch (requestDto) {
                case CommandDto commandDto -> processCommand(commandDto);
                case CallbackDto callbackDto -> processCallback(callbackDto);
                case MessageDto messageDto -> processMessage(messageDto);
                default -> throw new IllegalStateException("Unexpected value: " + requestDto);
            };
        } catch (BusinessException e) {
            log.error("Error while process message command {}", requestDto.getCommandType().getName(), e);
            return returnWarningMessage(e.getMessage());
        }
    }

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return requestTripId(commandDto.getTelegramId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return null;
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return null;
    }

    protected ResponseDto returnWarningMessage(String message) {
        return ResponseDto.builder()
                .text(message)
                .build();
    }

    protected ResponseDto returnErrorMessage() {
        return ResponseDto.builder()
                .text(BotAnswer.DEFAULT_ANSWER)
                .keyboard(defaultKeyboard())
                .build();
    }

    protected UserStateManager getUserStateManager() {
        return userStateManager;
    }

    protected ReplyKeyboardHistory getReplyKeyboardHistory() {
        return replyKeyboardHistory;
    }

    protected ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, getCommandType()))
                .build();
    }

    protected List<ReplyKeyboardBuilder.KeyboardButton> getFinalKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_TRIP.getDescription(),
                        format("%s/%s", CommandType.EDIT_TRIP.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.MY_TRIPS.getDescription(),
                        CommandType.MY_TRIPS.getName())
        );
    }

    protected List<ReplyKeyboardBuilder.KeyboardButton> defaultKeyboard() {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        NEW_TRIP.getDescription(), NEW_TRIP.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(
                        MY_TRIPS.getDescription(), MY_TRIPS.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(
                        HELP.getDescription(), HELP.getName())
        );
    }
}
