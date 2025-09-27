package com.planyourtrip.bot.service.command.hotel;

import com.planyourtrip.bot.dto.HotelDto;

import java.time.LocalDate;
import java.util.Collection;

public interface HotelService {

    void createHotel(int code, long tripId, long chatId);

    void setName(String name, long chatId);

    void setAddress(String address, long chatId);

    void setCheckInDate(LocalDate checkInDate, long chatId);

    void setCheckOutDate(LocalDate checkOutDate, long chatId);

    Collection<HotelDto> getHotelsByTripId(long tripId);

    HotelDto getHotelById(long id);

    void deleteHotel(long id);

    HotelDto commitNewHotel(long chatId);

}
