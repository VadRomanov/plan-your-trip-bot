package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.dto.domain.TicketType;
import com.planyourtrip.bot.service.command.impl.AbstractEditCommand;
import com.planyourtrip.bot.service.command.ticket.util.TicketUtil;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.DateTimeUtils;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditTicketCommand extends AbstractEditCommand {
    private final TicketService ticketService;

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (TicketUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TYPE -> processTypeResponse(messageDto);
            case AWAIT_DEPARTURE -> processDepartureResponse(messageDto);
            case AWAIT_ARRIVAL -> processArrivalResponse(messageDto);
            case AWAIT_DEPART_DATE -> processDepartDateResponse(messageDto);
            case AWAIT_DEPART_TIME -> processDepartTimeResponse(messageDto);
            case AWAIT_ARRIVE_DATE -> processArriveDateResponse(messageDto);
            case AWAIT_ARRIVE_TIME -> processArriveTimeResponse(messageDto);
            case AWAIT_FILE -> processFileResponse(messageDto);
        };
    }

    @Override
    protected ResponseDto requestEntityId(long tripId) {
        var tickets = ticketService.getTicketsByTripId(tripId);
        return ResponseDto.builder()
                .text(tickets.isEmpty() ? BotAnswer.MY_TICKETS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TICKET_REQUEST)
                .keyboard(tickets.isEmpty()
                        ? ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.ADD_TICKET)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(TicketUtil.mapTicketsToMap(tickets), tripId,
                        getCommandType()))
                .build();
    }

    @Override
    protected ResponseDto requestNewValue(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var ticketId = Long.parseLong(callbackDto.getCallbackData().get(2));
        var newValue = TicketUtil.State.valueOf(callbackDto.getCallbackData().get(3));
        ticketService.fetchTicketById(ticketId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(newValue.name()));
        if (newValue.equals(TicketUtil.State.AWAIT_TYPE)) {
            return ResponseDto.builder()
                    .text(BotAnswer.ADD_TICKET_TYPE_REQUEST)
                    .keyboard(TicketUtil.getTickerTypesKeyboard(tripId))
                    .build();
        }
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_REQUEST)
                .build();
    }

    @Override
    protected List<ReplyKeyboardBuilder.KeyboardButton> createStatesKeyboard(long tripId, long id) {
        var buttons = new ArrayList<ReplyKeyboardBuilder.KeyboardButton>();
        for (var state : TicketUtil.State.values()) {
            buttons.add(new ReplyKeyboardBuilder.KeyboardButton(state.getValue(),
                    String.format("%s/%s/%s/%s", getCommandType().getName(), tripId, id,
                            state)));
        }
        return buttons;
    }

    private ResponseDto processTypeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        ticket.setType(TicketType.valueOf(messageDto.getMsgText()));
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processDepartureResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        ticket.setDeparture(messageDto.getMsgText());
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processArrivalResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        ticket.setArrival(messageDto.getMsgText());
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processDepartDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        var departureDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        ticket.setDepartureTime(DateTimeUtils.addDate(ticket.getDepartureTime(), departureDate));
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processDepartTimeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        var departureTime = DateTimeUtils.parseTime(messageDto.getMsgText());
        ticket.setDepartureTime(DateTimeUtils.addTime(ticket.getDepartureTime(), departureTime));
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processArriveDateResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        var arrivalDate = DateTimeUtils.parseDate(messageDto.getMsgText());
        ticket.setArrivalTime(DateTimeUtils.addDate(ticket.getArrivalTime(), arrivalDate));
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processArriveTimeResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        var arrivalTime = DateTimeUtils.parseTime(messageDto.getMsgText());
        ticket.setArrivalTime(DateTimeUtils.addTime(ticket.getArrivalTime(), arrivalTime));
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processFileResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var ticket = ticketService.getTicketToUpdate(chatId);
        if (nonNull(messageDto.getDocument().getFileId())) {
            ticketService.setFileId(messageDto.getDocument().getFileId(), chatId);
        }
        ticketService.updateTicket(ticket, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_TICKET;
    }

}
