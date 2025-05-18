package com.plan.your.trip.bot.service;

import com.plan.your.trip.bot.exception.BusinessException;
import com.plan.your.trip.bot.exception.ResponseCode;
import com.plan.your.trip.bot.model.Trip;
import com.plan.your.trip.bot.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripService {


    private final TripRepository tripRepository;

    public Trip createNewTrip(String name, long userId) {
        var trip = new Trip(null, userId, name, null, null, null, null);
        try {
            return tripRepository.save(trip);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException
                    && nonNull(((ConstraintViolationException) e.getCause()).getConstraintName())
                    && ((ConstraintViolationException) e.getCause()).getConstraintName().contains("user_id_name_uq")) {
                throw BusinessException.builder(ResponseCode.USER_ID_AND_TRIP_NAME_UNIQUE_VIOLATION).build();
            } else {
                throw e;
            }
        }
    }

    public Trip setStartDt(long tripId, LocalDate startDt) {
        var trip = tripRepository.findById(tripId)
                .orElseThrow(() -> BusinessException.builder(ResponseCode.ENTITY_NOT_FOUND)
                        .params(List.of("trip", tripId))
                        .build());
        if (nonNull(trip.getEndDt()) && trip.getEndDt().isBefore(startDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .params(List.of("trip", tripId))
                    .build();
        }
        trip.setStartDt(startDt);
        return tripRepository.save(trip);
    }

    public Trip setEndDt(long tripId, LocalDate endDt) {
        var trip = tripRepository.findById(tripId)
                .orElseThrow(() -> BusinessException.builder(ResponseCode.ENTITY_NOT_FOUND)
                        .params(List.of("trip", tripId))
                        .build());
        if (nonNull(trip.getStartDt()) && trip.getStartDt().isAfter(endDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .params(List.of("trip", tripId))
                    .build();
        }
        trip.setEndDt(endDt);
        if (endDt.isBefore(LocalDate.now())) {
            trip.setExpired(true);
        }
        return tripRepository.save(trip);
    }

    public List<Trip> getTripsByUserId(long userId) {
        var trips = tripRepository.findAllByUserIdOrderByStartDt(userId);
        trips.stream()
                .filter(trip -> trip.getEndDt().isBefore(LocalDate.now()))
                .forEach(trip -> trip.setExpired(true));
        return tripRepository.saveAll(trips);
    }

    public Trip getTrip(long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder(ResponseCode.ENTITY_NOT_FOUND)
                        .params(List.of("trip", id))
                        .build());
    }

    public void deleteTrip(long id) {
        tripRepository.deleteById(id);
    }

}
