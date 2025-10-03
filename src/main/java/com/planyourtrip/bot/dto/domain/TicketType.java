package com.planyourtrip.bot.dto.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TicketType implements AbstractType {
    FLIGHT(0, "Самолет"),
    TRAIN(1, "Поезд"),
    BUS(2, "Автобус"),
    CAR(3, "Автомобиль"),
    OTHER(4, "Неизвестный тип транспорта");

    private final int code;
    private final String name;

    public static TicketType findByCode(int code) {
        return Arrays.stream(TicketType.values())
                .filter(value -> code == value.code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No TicketType found"));
    }
}
