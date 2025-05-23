package com.planyourtrip.bot.utils;

import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@UtilityClass
public class DateUtils {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, FORMAT);
        } catch (DateTimeParseException e) {
            log.error("Error wile parsing date {}", e.getMessage(), e);
            throw BusinessException.builder(ResponseCode.INVALID_DATE_FORMATE)
                    .params(List.of("дд.ММ.гггг", LocalDate.now().format(FORMAT)))
                    .build();
        }
    }
}
