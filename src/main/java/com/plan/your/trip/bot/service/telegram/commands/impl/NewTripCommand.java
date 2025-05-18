package com.plan.your.trip.bot.service.telegram.commands.impl;

import com.plan.your.trip.bot.exception.BusinessException;
import com.plan.your.trip.bot.service.TripService;
import com.plan.your.trip.bot.service.telegram.UserStateManager;
import com.plan.your.trip.bot.service.telegram.dto.CallbackDto;
import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import com.plan.your.trip.bot.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewTripCommand extends CommandProcessor {
    public static final String COMMAND_NAME = "newtrip";
    private static final String INIT_RESPONSE = "Введите название поездки (например, \"Барселона %s\")";
    private static final String NAME_RESPONSE = "Введите дату начала поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    private static final String START_DT_RESPONSE = "Введите дату конца поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    private static final String FINAL_RESPONSE =
            "Поездка <b>%s%s</b> создана!✅\nТеперь вы можете добавить бронирования и заметки.";
    private static final String TRIP_ID_KEY = "tripId";
    private static final String EXPIRED = " (Завершено)";

    private final TripService tripService;

    @Override
    public SendMessage process(MessageDto messageDto) {
        try {
            if (isNull(messageDto.state())) {
                return processInitResponse(messageDto.chatId());
            }

            return switch (State.valueOf(messageDto.state().getState())) {
                case NAME -> processNameResponse(messageDto);
                case START_DT -> processStartDtResponse(messageDto);
                case END_DT -> processEndDtResponse(messageDto);
            };
        } catch (BusinessException e) {
            log.error("Error while process command {}", COMMAND_NAME, e);
            return returnErrorMessage(e.getMessage(), messageDto);
        }

    }

    @Override
    public SendMessage processCallback(CallbackDto callbackDto) {
        return processInitResponse(callbackDto.chatId());
    }

    private SendMessage processInitResponse(long chatId) {
        getUserStateManager().setState(chatId,
                new UserStateManager.UserState(COMMAND_NAME, State.NAME.name(), null));
        return SendMessage.builder()
                .text(String.format(INIT_RESPONSE, LocalDateTime.now().getYear()))
                .chatId(chatId)
                .build();
    }

    private SendMessage processNameResponse(MessageDto messageDto) {
        long tripId;
        tripId = tripService.createNewTrip(messageDto.msgText(), messageDto.userId()).getId();
        getUserStateManager().setState(messageDto.chatId(),
                new UserStateManager.UserState(COMMAND_NAME, State.START_DT.name(), Map.of(TRIP_ID_KEY, tripId)));
        return SendMessage.builder()
                .text(NAME_RESPONSE)
                .chatId(messageDto.chatId())
                .replyToMessageId(messageDto.messageId())
                .build();
    }

    private SendMessage processStartDtResponse(MessageDto messageDto) {
        var state = messageDto.state();
        var tripId = (long) state.getStateEntities().get(TRIP_ID_KEY);
        var startDt = DateUtils.parseDate(messageDto.msgText());
        tripService.setStartDt(tripId, startDt);
        getUserStateManager().setState(messageDto.chatId(),
                new UserStateManager.UserState(COMMAND_NAME, State.END_DT.name(), Map.of(TRIP_ID_KEY, tripId)));
        return SendMessage.builder()
                .text(START_DT_RESPONSE)
                .chatId(messageDto.chatId())
                .replyToMessageId(messageDto.messageId())
                .build();
    }

    private SendMessage processEndDtResponse(MessageDto messageDto) {
        var state = messageDto.state();
        long tripId = (long) state.getStateEntities().get(TRIP_ID_KEY);
        var endDt = DateUtils.parseDate(messageDto.msgText());
        var trip = tripService.setEndDt(tripId, endDt);
        getUserStateManager().setState(messageDto.chatId(),
                new UserStateManager.UserState(COMMAND_NAME, State.END_DT.name(), Map.of(TRIP_ID_KEY, tripId)));
        return SendMessage.builder()
                .text(String.format(FINAL_RESPONSE, trip.getName(), trip.getExpired() ? EXPIRED : ""))
                .parseMode(ParseMode.HTML)
                .chatId(messageDto.chatId())
                .replyToMessageId(messageDto.messageId())
                .build();
    }

    private SendMessage returnErrorMessage(String message, MessageDto messageDto) {
        return SendMessage.builder()
                .text(message)
                .chatId(messageDto.chatId())
                .replyToMessageId(messageDto.messageId())
                .build();
    }

    @Override
    public String getProcessorName() {
        return COMMAND_NAME;
    }

    private enum State {
        NAME, START_DT, END_DT
    }
}
