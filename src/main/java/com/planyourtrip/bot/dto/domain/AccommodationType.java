package com.planyourtrip.bot.dto.domain;


import com.planyourtrip.bot.constant.BotAnswer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AccommodationType implements AbstractType {
    HOTEL(0, BotAnswer.ACCOMMODATION_TYPE_HOTEL),
    APARTMENTS(2, BotAnswer.ACCOMMODATION_TYPE_APARTMENTS);

    private final int code;
    private final String name;

    public static AccommodationType findByCode(int code) {
        return Arrays.stream(AccommodationType.values())
                .filter(value -> code == value.code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No AccommodationType found"));
    }
}
