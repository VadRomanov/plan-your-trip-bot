package com.planyourtrip.bot.service;

import com.planyourtrip.bot.dto.TripDto;

import java.time.LocalDate;
import java.util.Collection;

public interface TripService {

    void createTrip(String name, long userId);

    void setStartDt(LocalDate startDt);

    void setEndDt(LocalDate endDt);

    Collection<TripDto> getTripsByUserId(long userId);

    TripDto getTripById(long id);

    void deleteTrip(long id);

    TripDto commitNewTrip();

}
