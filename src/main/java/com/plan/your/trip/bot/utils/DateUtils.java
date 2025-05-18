package com.plan.your.trip.bot.utils;

import com.plan.your.trip.bot.exception.BusinessException;
import com.plan.your.trip.bot.exception.ResponseCode;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@UtilityClass
public class DateUtils {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, FORMAT);
        } catch (DateTimeParseException e) {
            throw BusinessException.builder(ResponseCode.INVALID_DATE_FORMATE)
                    .params(List.of("дд.ММ.гггг", LocalDate.now().format(FORMAT)))
                    .build();
        }
    }
}
