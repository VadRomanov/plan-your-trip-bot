package com.planyourtrip.bot.service.command.ticket;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.dto.domain.TicketDto;
import com.planyourtrip.bot.service.command.impl.AbstractMyCommand;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyTicketsCommand extends AbstractMyCommand {
    private final TicketService ticketService;

    @Override
    protected ResponseDto prepareAnswer(long tripId) {
        var tickets = ticketService.getTicketsByTripId(tripId);
        if (tickets.isEmpty()) {
            return ResponseDto.builder()
                    .text(BotAnswer.MY_TICKETS_EMPTY_RESPONSE)
                    .keyboard(ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.ADD_TICKET))
                    .build();
        }
        return ResponseDto.builder()
                .text(String.format(BotAnswer.MY_TICKETS_RESPONSE,
                        String.join(",",
                                tickets.stream()
                                        .map(TicketDto::toString)
                                        .toList())))
                .keyboard(ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.EDIT_TICKET,
                        CommandType.DELETE_TICKET))
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_TICKETS;
    }
}
