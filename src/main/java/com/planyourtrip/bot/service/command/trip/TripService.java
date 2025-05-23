package com.planyourtrip.bot.service.command.trip;

import com.planyourtrip.bot.dto.TripDto;

import java.time.LocalDate;
import java.util.Collection;

public interface TripService {

    void createTrip(String name, long userId, long chatId);

    void setStartDt(LocalDate startDt, long chatId);

    void setEndDt(LocalDate endDt, long chatId);

    Collection<TripDto> getTripsByTelegramId(long telegramId);

    TripDto getTripById(long id);

    void deleteTrip(long id);

    TripDto commitNewTrip(long chatId);

}
