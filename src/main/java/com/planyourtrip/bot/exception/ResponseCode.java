package com.planyourtrip.bot.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Getter
public enum ResponseCode {

    USER_ID_AND_TRIP_NAME_UNIQUE_VIOLATION(1, "У вас уже есть поездка с таким названием. " +
            "Требуется уникальное название. Уточните, пожалуйста."),
    INVALID_DATE_FORMATE(2, "Неверный формат даты. Укажите, пожалуйста, дату в формате %s (например, %s)."),
    ENTITY_NOT_FOUND(3, "Запись в таблице %s не найдена. Id=%s."),
    INVALID_TIMELINE(4, "К сожалению, путешествия в прошлое пока что запрещены \uD83D\uDE01.");

    static {
        final var set = new HashSet<Integer>();
        for (ResponseCode value : ResponseCode.values()) {
            if (!set.add(value.code)) {
                log.error("ResponseCode already exist {}", value.code);
                System.exit(1);
            }
        }
    }

    private final int code;
    private final String description;


    public String getDescription(final List<Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            return description;
        } else {
            return String.format(description, params.toArray(new Object[0]));
        }
    }
}
