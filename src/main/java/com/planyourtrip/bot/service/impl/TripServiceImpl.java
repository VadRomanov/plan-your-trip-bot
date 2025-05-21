package com.planyourtrip.bot.service.impl;

import com.planyourtrip.bot.dto.TripDto;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import com.planyourtrip.bot.service.TripService;
import com.planyourtrip.bot.service.core.CoreServiceTripClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final CoreServiceTripClient tripClient;

    private static final ThreadLocal<TripDto> TRIP_DTO_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public void createTrip(String name, long userId) {
        TRIP_DTO_THREAD_LOCAL.set(new TripDto(name, Set.of(userId)));
    }

    @Override
    public void setStartDt(LocalDate startDt) {
        var trip = TRIP_DTO_THREAD_LOCAL.get();
        if (nonNull(trip.getEndDate()) && trip.getEndDate().isBefore(startDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        trip.setStartDate(startDt);
        TRIP_DTO_THREAD_LOCAL.set(trip);
    }

    @Override
    public void setEndDt(LocalDate endDt) {
        var trip = TRIP_DTO_THREAD_LOCAL.get();
        if (nonNull(trip.getStartDate()) && trip.getStartDate().isAfter(endDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        trip.setEndDate(endDt);
        TRIP_DTO_THREAD_LOCAL.set(trip);
    }

    @Override
    public Collection<TripDto> getTripsByUserId(long userId) {
        var trips = tripClient.getTripsByUser(userId);
        trips.forEach(trip -> {
            if (trip.getEndDate().isBefore(LocalDate.now())) {
                trip.setExpired(true);
            }
        });

        return trips;
    }

    @Override
    public TripDto getTripById(long id) {
        var trip = tripClient.getTrip(id);
        if (trip.getEndDate().isBefore(LocalDate.now())) {
            trip.setExpired(true);
        }
        return trip;
    }

    @Override
    public void deleteTrip(long id) {
        tripClient.deleteTrip(id);
    }

    public TripDto commitNewTrip() {
        var trip = TRIP_DTO_THREAD_LOCAL.get();
        return tripClient.createTrip(trip);
    }
}
