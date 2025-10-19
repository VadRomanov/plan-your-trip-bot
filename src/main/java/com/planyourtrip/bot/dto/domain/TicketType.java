package com.planyourtrip.bot.dto.domain;

import com.planyourtrip.bot.constant.BotAnswer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TicketType implements AbstractType {
    FLIGHT(0, BotAnswer.TICKET_TYPE_PLANE),
    TRAIN(1, BotAnswer.TICKET_TYPE_TRAIN),
    BUS(2, BotAnswer.TICKET_TYPE_BUS),
    CAR(3, BotAnswer.TICKET_TYPE_CAR),
    OTHER(4, BotAnswer.TICKET_TYPE_OTHER);

    private final int code;
    private final String name;

    public static TicketType findByCode(int code) {
        return Arrays.stream(TicketType.values())
                .filter(value -> code == value.code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No TicketType found"));
    }
}
