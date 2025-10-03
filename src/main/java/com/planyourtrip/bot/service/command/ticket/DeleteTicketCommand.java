package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractDeleteCommand;
import com.planyourtrip.bot.service.command.ticket.util.TicketUtil;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteTicketCommand extends AbstractDeleteCommand {
    private final TicketService ticketService;

    @Override
    protected ResponseDto requestEntityId(long tripId) {
        var tickets = ticketService.getTicketsByTripId(tripId);
        return ResponseDto.builder()
                .text(tickets.isEmpty() ? BotAnswer.MY_TICKETS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TICKET_REQUEST)
                .keyboard(tickets.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_TICKET)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(TicketUtil.mapTicketsToMap(tickets), tripId,
                        getCommandType()))
                .build();
    }

    @Override
    protected ResponseDto requestConfirmation(long id) {
        var ticket = ticketService.getTicketById(id);
        var text = String.format(BotAnswer.DELETE_TICKET_CONFIRMATION_REQUEST, ticket);
        return requestConfirmation(text, ticket.getTripId(), id);
    }

    @Override
    protected ResponseDto doDelete(long id) {
        ticketService.deleteTicket(id);
        return ResponseDto.builder()
                .text(BotAnswer.DELETE_TICKET_FINAL_RESPONSE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_TICKET;
    }
}
