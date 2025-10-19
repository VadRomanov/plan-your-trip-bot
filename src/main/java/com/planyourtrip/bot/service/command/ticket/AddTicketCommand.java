package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractAddCommand;
import com.planyourtrip.bot.service.command.ticket.util.TicketUtil;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddTicketCommand extends AbstractAddCommand {
    private final TicketService ticketService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            var stepValue = callbackDto.getCallbackData().get(1);
            if (COMPLETE_SKIP_VALUES.contains(stepValue)) {
                return processSkipOrCompleteResponse(callbackDto.getChatId(), stepValue);
            } else {
                return processTripIdResponse(Long.parseLong(stepValue));
            }
        } else if (step == 3) {
            return processTypeResponse(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (TicketUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_DEPARTURE -> processDepartureResponse(messageDto);
            case AWAIT_ARRIVAL -> processArrivalResponse(messageDto);
            case AWAIT_DEPART_DATE -> processDepartDateResponse(messageDto);
            case AWAIT_DEPART_TIME -> processDepartTimeResponse(messageDto);
            case AWAIT_ARRIVE_DATE -> processArriveDateResponse(messageDto);
            case AWAIT_ARRIVE_TIME -> processArriveTimeResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
            case AWAIT_TYPE -> returnErrorMessage();
        };
    }

    private ResponseDto processTripIdResponse(long tripId) {
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_TYPE_REQUEST)
                .keyboard(TicketUtil.getTickerTypesKeyboard(tripId))
                .build();
    }

    private ResponseDto processTypeResponse(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var ticketCode = Integer.parseInt(callbackDto.getCallbackData().get(2));
        ticketService.createTicket(ticketCode, tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_DEPARTURE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_DEPARTURE_REQUEST)
                .build();
    }

    private ResponseDto processDepartureResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        ticketService.setDeparture(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_ARRIVAL.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_ARRIVAL_REQUEST)
                .build();
    }

    private ResponseDto processArrivalResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        ticketService.setArrival(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_DEPART_DATE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_DEPARTURE_DATE_REQUEST)
                .keyboard(ReplyKeyboardBuilder.buildSkipAndCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processDepartDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var departDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        ticketService.setDepartDate(departDate, chatId);
        return prepareDepartTimeRequest(chatId);
    }

    private ResponseDto prepareDepartTimeRequest(long chatId) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_DEPART_TIME.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_DEPARTURE_TIME_REQUEST))
                .keyboard(ReplyKeyboardBuilder.buildSkipAndCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processDepartTimeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var departTime = DateTimeUtils.parseTime(messageDto.getMsgText());
        ticketService.setDepartTime(departTime, chatId);
        return prepareArriveDateRequest(chatId);
    }

    private ResponseDto prepareArriveDateRequest(long chatId) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_ARRIVE_DATE.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_ARRIVAL_DATE_REQUEST))
                .keyboard(ReplyKeyboardBuilder.buildSkipAndCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processArriveDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var arriveDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        ticketService.setArriveDate(arriveDate, chatId);
        return prepareArriveTimeRequest(chatId);
    }

    private ResponseDto prepareArriveTimeRequest(long chatId) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_ARRIVE_TIME.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_ARRIVAL_TIME_REQUEST))
                .keyboard(ReplyKeyboardBuilder.buildSkipAndCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processArriveTimeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var arriveTime = DateTimeUtils.parseTime(messageDto.getMsgText());
        ticketService.setArriveTime(arriveTime, chatId);
        return prepareFileRequest(chatId);
    }

    private ResponseDto prepareFileRequest(long chatId) {
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_FILE.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_FILE_REQUEST))
                .keyboard(ReplyKeyboardBuilder.buildCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        if (nonNull(messageDto.getDocument().getFileId())) {
            ticketService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
        return prepareFinalResponse(chatId);
    }

    @Override
    protected ResponseDto prepareFinalResponse(long chatId) {
        var ticket = ticketService.commitNewTicket(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_FINAL_RESPONSE)
                .keyboard(getFinalKeyboard(ticket.getTripId()))
                .build();
    }

    @Override
    protected ResponseDto processSkip(long chatId) {
        var state = getUserStateManager().getState(chatId);
        return switch (TicketUtil.State.valueOf(state.getState())) {
            case AWAIT_DEPART_DATE -> prepareDepartTimeRequest(chatId);
            case AWAIT_DEPART_TIME -> prepareArriveDateRequest(chatId);
            case AWAIT_ARRIVE_DATE -> prepareArriveTimeRequest(chatId);
            case AWAIT_ARRIVE_TIME -> prepareFileRequest(chatId);
            case AWAIT_DEPARTURE, AWAIT_ARRIVAL, AWAIT_FILE, AWAIT_TYPE -> returnErrorMessage();
        };
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_TICKET;
    }

}
