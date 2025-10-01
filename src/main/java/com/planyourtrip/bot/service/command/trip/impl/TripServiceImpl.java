package com.planyourtrip.bot.service.command.trip.impl;

import com.planyourtrip.bot.dto.TripDto;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import com.planyourtrip.bot.service.command.trip.TripService;
import com.planyourtrip.bot.service.core.TripCoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripCoreClient tripCoreClient;

    private static final Map<Long, TripDto> TRIP_DTO_CHAT_CONTAINER = new ConcurrentHashMap<>();
    private static final Map<Long, TripDto> TRIP_DTO_CHAT_UPDATE_CONTAINER = new ConcurrentHashMap<>();

    @Override
    public void createTrip(String name, long userId, long chatId) {
        log.debug("Create trip {}, chatId {}", name, chatId);
        TRIP_DTO_CHAT_CONTAINER.put(chatId, new TripDto(name, Set.of(userId)));
    }

    @Override
    public void setStartDt(LocalDate startDt, long chatId) {
        var trip = TRIP_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set trip {} start date {}, chatId {}", trip.getName(), startDt, chatId);
        if (nonNull(trip.getEndDate()) && trip.getEndDate().isBefore(startDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        trip.setStartDate(startDt);
        TRIP_DTO_CHAT_CONTAINER.put(chatId, trip);
    }

    @Override
    public void setEndDt(LocalDate endDt, long chatId) {
        var trip = TRIP_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set trip {} end date {}, chatId {}", trip.getName(), endDt, chatId);
        if (nonNull(trip.getStartDate()) && trip.getStartDate().isAfter(endDt)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        trip.setEndDate(endDt);
        TRIP_DTO_CHAT_CONTAINER.put(chatId, trip);
    }

    @Override
    public Collection<TripDto> getTripsByTelegramId(long telegramId) {
        log.debug("Get trips by telegramId {}", telegramId);
        var trips = tripCoreClient.getTripsByUser(telegramId);
        log.info("{} trips obtained by telegramId {}", trips.size(), telegramId);
        return trips;
    }

    @Override
    public TripDto getTripById(long id) {
        log.debug("Get trip by id {}", id);
        var trip = tripCoreClient.getTrip(id);
        log.info("Trip obtained by id {}", trip);
        return trip;
    }

    @Override
    public void fetchTripById(long id, long chatId) {
        var trip = getTripById(id);
        TRIP_DTO_CHAT_UPDATE_CONTAINER.put(chatId, trip);
    }

    @Override
    public void deleteTrip(long id) {
        log.debug("Delete trip by id {}", id);
        tripCoreClient.deleteTrip(id);
        log.info("Trip deleted by id {}", id);
    }

    public TripDto commitNewTrip(long chatId) {
        log.debug("Commit trip for chatId {}", chatId);
        var trip = TRIP_DTO_CHAT_CONTAINER.get(chatId);
        var savedTrip = tripCoreClient.createTrip(trip);
        TRIP_DTO_CHAT_CONTAINER.remove(chatId);

        log.info("Trip {} for chatId {} commited", trip, chatId);
        return savedTrip;
    }

    @Override
    public TripDto getTripToUpdate(long chatId) {
        return TRIP_DTO_CHAT_UPDATE_CONTAINER.get(chatId);
    }

    @Override
    public void updateTrip(TripDto trip, long chatId) {
        log.debug("Update trip {}", trip);
        var updatedTrip = tripCoreClient.updateTrip(trip.getId(), trip);
        TRIP_DTO_CHAT_UPDATE_CONTAINER.remove(chatId);
        log.info("Trip {} updated", updatedTrip);
    }
}
