package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.ticket.util.TicketUtil;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddTicketCommand extends AbstractCommand {
    private final TicketService ticketService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return processTripIdResponse(Long.parseLong(callbackDto.getCallbackData().get(1)));
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
            case AWAIT_DEPART_DATE -> processDepartureDateResponse(messageDto);
            case AWAIT_DEPART_TIME -> processDepartureTimeResponse(messageDto);
            case AWAIT_ARRIVAL -> processArrivalResponse(messageDto);
            case AWAIT_ARRIVE_DATE -> processArrivalDateResponse(messageDto);
            case AWAIT_ARRIVE_TIME -> processArrivalTimeResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
            case AWAIT_TYPE -> returnErrorMessage();
        };
    }

    private ResponseDto processTripIdResponse(long tripId) {
        var keyboard = TicketUtil.getTickerTypesKeyboard(tripId);
        keyboard.add(new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.SKIP,
                String.format("%s/%s", getCommandType().getName(), TicketUtil.State.AWAIT_TYPE.name())));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_TYPE_REQUEST)
                .keyboard(keyboard)
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
                .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.SKIP,
                        String.format("%s/%s", getCommandType().getName(), TicketUtil.State.AWAIT_DEPARTURE.name()))))
                .build();
    }

    private ResponseDto processDepartureResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        ticketService.setDeparture(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_DEPART_DATE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_DEPARTURE_DATE_REQUEST)
                .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.SKIP,
                        String.format("%s/%s", getCommandType().getName(), TicketUtil.State.AWAIT_DEPART_DATE.name()))))
                .build();
    }

    private ResponseDto processDepartureDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var departureDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        ticketService.setDepartDate(departureDate, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_DEPART_TIME.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_DEPARTURE_TIME_REQUEST))
                .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.SKIP,
                        String.format("%s/%s", getCommandType().getName(), TicketUtil.State.AWAIT_DEPART_TIME.name()))))
                .build();
    }

    private ResponseDto processDepartureTimeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var departureTime = DateTimeUtils.parseTime(messageDto.getMsgText());
        ticketService.setDepartTime(departureTime, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_ARRIVAL.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_ARRIVAL_REQUEST))
                .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.SKIP,
                        String.format("%s/%s", getCommandType().getName(), TicketUtil.State.AWAIT_ARRIVAL.name()))))
                .build();
    }

    private ResponseDto processArrivalResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        ticketService.setArrival(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_ARRIVE_DATE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_ARRIVAL_DATE_REQUEST)
                .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.SKIP,
                        String.format("%s/%s", getCommandType().getName(), TicketUtil.State.AWAIT_ARRIVE_DATE.name()))))
                .build();
    }

    private ResponseDto processArrivalDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var arrivalDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        ticketService.setArriveDate(arrivalDate, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_ARRIVE_TIME.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_ARRIVAL_TIME_REQUEST))
                .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.SKIP,
                        String.format("%s/%s", getCommandType().getName(), TicketUtil.State.AWAIT_ARRIVE_TIME.name()))))
                .build();
    }

    private ResponseDto processArrivalTimeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var arrivalTime = DateTimeUtils.parseTime(messageDto.getMsgText());
        ticketService.setArriveTime(arrivalTime, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(TicketUtil.State.AWAIT_FILE.name()));
        return ResponseDto.builder()
                .text(format(BotAnswer.ADD_TICKET_FILE_REQUEST))
                .keyboard(List.of(new ReplyKeyboardBuilder.KeyboardButton(BotAnswer.SKIP,
                        String.format("%s/%s", getCommandType().getName(), TicketUtil.State.AWAIT_FILE.name()))))
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        if (nonNull(messageDto.getDocument().getFileId())) {
            ticketService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
        var ticket = ticketService.commitNewTicket(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(BotAnswer.ADD_TICKET_FINAL_RESPONSE)
                .keyboard(getFinalKeyboard(ticket.getTripId()))
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_TICKET;
    }

}
