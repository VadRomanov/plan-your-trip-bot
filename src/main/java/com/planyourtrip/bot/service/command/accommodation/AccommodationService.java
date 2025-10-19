package com.planyourtrip.bot.service.command.accommodation;

import com.planyourtrip.bot.dto.domain.AccommodationDto;

import java.time.LocalDate;
import java.util.Collection;

public interface AccommodationService {

    void createAccommodation(int type, long tripId, long chatId);

    void setName(String name, long chatId);

    void setAddress(String address, long chatId);

    void setCheckInDate(LocalDate checkInDate, long chatId);

    void setCheckOutDate(LocalDate checkOutDate, long chatId);

    void setFileId(String fileId, long chatId);

    Collection<AccommodationDto> getAccommodationsByTripId(long tripId);

    AccommodationDto getAccommodationById(long id);

    void fetchAccommodationById(long id, long chatId);

    void deleteAccommodation(long id);

    AccommodationDto commitNewAccommodation(long chatId);

    AccommodationDto getAccommodationToUpdate(long chatId);

    void updateAccommodation(AccommodationDto accommodation, long chatId);
}
