package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.impl.TripServiceImpl;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewTripCommand extends AbstractCommand {

    private final TripServiceImpl tripService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processInitResponse(commandDto.getChatId());
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        try {
            return switch (State.valueOf(messageDto.getState().getState())) {
                case NAME -> processNameResponse(messageDto);
                case START_DT -> processStartDtResponse(messageDto);
                case END_DT -> processEndDtResponse(messageDto);
            };
        } catch (BusinessException e) {
            log.error("Error while process message command {}", CommandType.NEW_TRIP.getName(), e);
            return returnErrorMessage(e.getMessage(), messageDto);
        }
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return processInitResponse(callbackDto.getChatId());
    }

    private ResponseDto processInitResponse(long chatId) {
        getUserStateManager().setState(chatId,
                new UserState(CommandType.NEW_TRIP, State.NAME.name(), null));
        return ResponseDto.builder()
                .text(String.format(BotAnswer.NEW_TRIP_INIT_RESPONSE, LocalDateTime.now().getYear()))
                .chatId(chatId)
                .build();
    }

    private ResponseDto processNameResponse(MessageDto messageDto) {
        tripService.createTrip(messageDto.getMsgText(), messageDto.getUserId());
        getUserStateManager().setState(messageDto.getChatId(),
                new UserState()
                        .setResponsibleCommand(CommandType.NEW_TRIP)
                        .setState(State.START_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_TRIP_NAME_RESPONSE)
                .chatId(messageDto.getChatId())
                .replyToMessageId(messageDto.getMessageId())
                .build();
    }

    private ResponseDto processStartDtResponse(MessageDto messageDto) {
        var startDt = DateUtils.parseDate(messageDto.getMsgText());
        tripService.setStartDt(startDt);
        getUserStateManager().setState(messageDto.getChatId(),
                new UserState()
                        .setResponsibleCommand(CommandType.NEW_TRIP)
                        .setState(State.END_DT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_TRIP_START_DT_RESPONSE)
                .chatId(messageDto.getChatId())
                .replyToMessageId(messageDto.getMessageId())
                .build();
    }

    private ResponseDto processEndDtResponse(MessageDto messageDto) {
        var endDt = DateUtils.parseDate(messageDto.getMsgText());
        tripService.setEndDt(endDt);
        var trip = tripService.commitNewTrip();
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(String.format(BotAnswer.NEW_TRIP_FINAL_RESPONSE, trip.getName()))
                .chatId(messageDto.getChatId())
                .replyToMessageId(messageDto.getMessageId())
                .build();
    }

    private ResponseDto returnErrorMessage(String message, MessageDto messageDto) {
        return ResponseDto.builder()
                .text(message)
                .chatId(messageDto.getChatId())
                .replyToMessageId(messageDto.getMessageId())
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.NEW_TRIP;
    }

    private enum State {
        NAME, START_DT, END_DT
    }
}
