package com.planyourtrip.bot.service.command.ticket.util;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.domain.TicketDto;
import com.planyourtrip.bot.dto.domain.TicketType;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class TicketUtil {

    public Map<Long, String> mapTicketsToMap(Collection<TicketDto> tickets) {
        return tickets.stream()
                .collect(Collectors.toMap(TicketDto::getId, TicketDto::toString));
    }

    public List<ReplyKeyboardBuilder.KeyboardButton> getTickerTypesKeyboard(long tripId) {
        return ReplyKeyboardBuilder.buildTypesButtons(
                Arrays.stream(TicketType.values()).toList(),
                CommandType.ADD_TICKET,
                tripId);
    }

    @Getter
    @RequiredArgsConstructor
    public enum State {
        AWAIT_TYPE("Тип"),
        AWAIT_DEPARTURE("Место отправления"),
        AWAIT_ARRIVAL("Место прибытия"),
        AWAIT_DEPART_DATE("Дата отправления"),
        AWAIT_DEPART_TIME("Время отправления"),
        AWAIT_ARRIVE_DATE("Дата прибытия"),
        AWAIT_ARRIVE_TIME("Время прибытия"),
        AWAIT_FILE("Файл");

        private final String value;
    }

}
