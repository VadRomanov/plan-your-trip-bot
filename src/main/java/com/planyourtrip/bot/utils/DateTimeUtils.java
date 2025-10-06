package com.planyourtrip.bot.utils;

import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@UtilityClass
public class DateTimeUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            log.error("Error wile parsing date {}", e.getMessage(), e);
            throw BusinessException.builder(ResponseCode.INVALID_DATE_FORMATE)
                    .params(List.of(DATE_FORMAT.toString(), LocalDate.now().format(DATE_FORMAT)))
                    .build();
        }
    }

    public LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            log.error("Error wile parsing time {}", e.getMessage(), e);
            throw BusinessException.builder(ResponseCode.INVALID_DATE_FORMATE)
                    .params(List.of(DATE_FORMAT.toString(), LocalDate.now().format(DATE_FORMAT)))
                    .build();
        }
    }

    public OffsetDateTime toOffsetDateTime(LocalDate date) {
        return date.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    public OffsetDateTime addTime(OffsetDateTime dateTime, LocalTime time) {
        return dateTime.withHour(time.getHour())
                .withMinute(time.getMinute());
    }

    public OffsetDateTime addDate(OffsetDateTime dateTime, LocalDate date) {
        return dateTime.withDayOfMonth(date.getDayOfMonth())
                .withMonth(date.getMonthValue())
                .withYear(date.getYear());
    }
}
