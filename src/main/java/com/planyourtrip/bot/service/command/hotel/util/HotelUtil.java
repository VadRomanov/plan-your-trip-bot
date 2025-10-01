package com.planyourtrip.bot.service.command.hotel.util;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.AccommodationType;
import com.planyourtrip.bot.dto.HotelDto;
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
public class HotelUtil {

    public static Map<Long, String> mapHotelsToMap(Collection<HotelDto> hotels) {
        return hotels.stream()
                .collect(Collectors.toMap(HotelDto::getId, HotelDto::toString));
    }

    public static List<ReplyKeyboardBuilder.KeyboardButton> getAccommodationTypesKeyboard(long tripId) {
        return ReplyKeyboardBuilder.buildTypesButtons(
                Arrays.stream(AccommodationType.values()).toList(),
                CommandType.ADD_HOTEL,
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
