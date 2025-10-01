package com.planyourtrip.bot.service.command.trip.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TripUtil {

    @Getter
    @RequiredArgsConstructor
    public enum State {
        AWAIT_NAME("Название"),
        AWAIT_START_DT("Дата начала"),
        AWAIT_END_DT("Дана окончания");

        private final String value;
    }

}
