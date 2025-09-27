package com.planyourtrip.bot.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AccommodationType implements AbstractType {
    HOTEL(0, "Отель"),
    APARTMENTS(2, "Апартаменты");

    private final int code;
    private final String name;

    public static AccommodationType findByCode(int code) {
        return Arrays.stream(AccommodationType.values())
                .filter(value -> code == value.code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No AccommodationType found"));
    }
}
