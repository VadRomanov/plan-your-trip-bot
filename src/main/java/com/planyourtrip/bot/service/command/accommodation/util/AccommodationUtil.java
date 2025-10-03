package com.planyourtrip.bot.service.command.accommodation.util;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.domain.AccommodationType;
import com.planyourtrip.bot.dto.domain.AccommodationDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class AccommodationUtil {

    public Map<Long, String> mapAccommodationsToMap(Collection<AccommodationDto> accommodations) {
        return accommodations.stream()
                .collect(Collectors.toMap(AccommodationDto::getId, AccommodationDto::toString));
    }

    public List<ReplyKeyboardBuilder.KeyboardButton> getAccommodationTypesKeyboard(long tripId) {
        return ReplyKeyboardBuilder.buildTypesButtons(
                Arrays.stream(AccommodationType.values()).toList(),
                CommandType.ADD_ACCOMMODATION,
                tripId);
    }

    @Getter
    @RequiredArgsConstructor
    public enum State {
        AWAIT_TYPE("Тип"),
        AWAIT_NAME("Название"),
        AWAIT_CHECK_IN("Дата заселения"),
        AWAIT_CHECK_OUT("Дата выезда"),
        AWAIT_ADDRESS("Адрес"),
        AWAIT_FILE("Подтверждение");

        private final String value;
    }

}
