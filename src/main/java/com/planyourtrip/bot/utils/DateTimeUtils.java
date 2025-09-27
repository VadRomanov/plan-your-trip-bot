package com.planyourtrip.bot.utils;

import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@UtilityClass
public class DateTimeUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            log.error("Error wile parsing date {}", e.getMessage(), e);
            throw BusinessException.builder(ResponseCode.INVALID_DATE_FORMATE)
                    .params(List.of(DATE_FORMAT.toString(), LocalDate.now().format(DATE_FORMAT)))
                    .build();
        }
    }

    public static OffsetDateTime parseDatetime(String value) {
        try {
            var localDateTime = LocalDateTime.parse(value, DATE_TIME_FORMAT);
            return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            log.error("Error wile parsing datetime {}", e.getMessage(), e);
            throw BusinessException.builder(ResponseCode.INVALID_DATE_FORMATE)
                    .params(List.of(DATE_TIME_FORMAT.toString(), LocalDate.now().format(DATE_TIME_FORMAT)))
                    .build();
        }
    }
}
