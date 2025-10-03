package com.planyourtrip.bot.service.command.accommodation.impl;

import com.planyourtrip.bot.dto.domain.AccommodationType;
import com.planyourtrip.bot.dto.domain.AccommodationDto;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import com.planyourtrip.bot.service.command.accommodation.AccommodationService;
import com.planyourtrip.bot.service.core.AccommodationCoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationCoreClient accommodationCoreClient;

    private static final Map<Long, AccommodationDto> ACCOMMODATION_DTO_CHAT_CONTAINER = new ConcurrentHashMap<>();
    private static final Map<Long, AccommodationDto> ACCOMMODATION_DTO_CHAT_UPDATE_CONTAINER = new ConcurrentHashMap<>();

    @Override
    public void createAccommodation(int code, long tripId, long chatId) {
        log.debug("Create accommodation, chatId {}", chatId);
        ACCOMMODATION_DTO_CHAT_CONTAINER.put(chatId, new AccommodationDto(tripId).setType(AccommodationType.findByCode(code)));
    }

    @Override
    public void setName(String name, long chatId) {
        var accommodation = ACCOMMODATION_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set accommodation name {}, chatId {}", name, chatId);
        accommodation.setName(name);
        ACCOMMODATION_DTO_CHAT_CONTAINER.put(chatId, accommodation);
    }

    @Override
    public void setAddress(String address, long chatId) {
        var accommodation = ACCOMMODATION_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set accommodation address {}, chatId {}", address, chatId);
        accommodation.setAddress(address);
        ACCOMMODATION_DTO_CHAT_CONTAINER.put(chatId, accommodation);
    }

    @Override
    public void setCheckInDate(LocalDate checkInDate, long chatId) {
        var accommodation = ACCOMMODATION_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set accommodation check-in date {}, chatId {}", checkInDate, chatId);
        if (nonNull(accommodation.getCheckOutDate()) && accommodation.getCheckOutDate().isBefore(checkInDate)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        accommodation.setCheckInDate(checkInDate);
        ACCOMMODATION_DTO_CHAT_CONTAINER.put(chatId, accommodation);
    }

    @Override
    public void setCheckOutDate(LocalDate checkOutDate, long chatId) {
        var accommodation = ACCOMMODATION_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set accommodation check-out date {}, chatId {}", checkOutDate, chatId);
        if (nonNull(accommodation.getCheckInDate()) && accommodation.getCheckInDate().isAfter(checkOutDate)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        accommodation.setCheckOutDate(checkOutDate);
        ACCOMMODATION_DTO_CHAT_CONTAINER.put(chatId, accommodation);
    }

    @Override
    public void setFileId(String fileId, long chatId) {
        var accommodation = ACCOMMODATION_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set file_id {}, chatId {}", fileId, chatId);
        accommodation.setFileUrl(fileId);
        ACCOMMODATION_DTO_CHAT_CONTAINER.put(chatId, accommodation);
    }

    @Override
    public Collection<AccommodationDto> getAccommodationsByTripId(long tripId) {
        log.debug("Get accommodations by tripId {}", tripId);
        var accommodations = accommodationCoreClient.getAccommodationsByTrip(tripId);
        log.info("{} accommodations obtained by tripId {}", accommodations.size(), tripId);
        return accommodations;
    }

    @Override
    public AccommodationDto getAccommodationById(long id) {
        log.debug("Get accommodation by id {}", id);
        var accommodation = accommodationCoreClient.getAccommodationById(id);
        log.info("Accommodation obtained by id {}", id);
        return accommodation;
    }

    @Override
    public void fetchAccommodationById(long id, long chatId) {
        var accommodation = getAccommodationById(id);
        ACCOMMODATION_DTO_CHAT_UPDATE_CONTAINER.put(chatId, accommodation);
    }

    @Override
    public void deleteAccommodation(long id) {
        log.debug("Delete accommodation by id {}", id);
        accommodationCoreClient.deleteAccommodation(id);
        log.info("accommodation deleted by id {}", id);
    }

    @Override
    public AccommodationDto commitNewAccommodation(long chatId) {
        log.debug("Commit accommodation for chatId {}", chatId);
        var accommodation = ACCOMMODATION_DTO_CHAT_CONTAINER.get(chatId);
        var savedAccommodation = accommodationCoreClient.createAccommodation(accommodation);
        ACCOMMODATION_DTO_CHAT_CONTAINER.remove(chatId);

        log.info("Accommodation {} for chatId {} commited", accommodation, savedAccommodation);
        return savedAccommodation;
    }

    @Override
    public AccommodationDto getAccommodationToUpdate(long chatId) {
        return ACCOMMODATION_DTO_CHAT_UPDATE_CONTAINER.get(chatId);
    }

    @Override
    public void updateAccommodation(AccommodationDto accommodation, long chatId) {
        log.debug("Update accommodation {}", accommodation);
        var updatedAccommodation = accommodationCoreClient.updateAccommodation(accommodation.getId(), accommodation);
        ACCOMMODATION_DTO_CHAT_UPDATE_CONTAINER.remove(chatId);
        log.info("updatedAccommodation {} updated", updatedAccommodation);
    }
}
