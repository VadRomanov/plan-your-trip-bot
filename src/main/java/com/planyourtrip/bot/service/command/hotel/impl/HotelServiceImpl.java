package com.planyourtrip.bot.service.command.hotel.impl;

import com.planyourtrip.bot.dto.AccommodationType;
import com.planyourtrip.bot.dto.HotelDto;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import com.planyourtrip.bot.service.command.hotel.HotelService;
import com.planyourtrip.bot.service.core.HotelCoreClient;
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
public class HotelServiceImpl implements HotelService {
    private final HotelCoreClient hotelCoreClient;

    private static final Map<Long, HotelDto> HOTEL_DTO_CHAT_CONTAINER = new ConcurrentHashMap<>();

    @Override
    public void createHotel(int code, long tripId, long chatId) {
        log.debug("Create hotel, chatId {}", chatId);
        HOTEL_DTO_CHAT_CONTAINER.put(chatId, new HotelDto(tripId).setType(AccommodationType.findByCode(code)));
    }

    @Override
    public void setName(String name, long chatId) {
        var hotel = HOTEL_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set hotel name {}, chatId {}", name, chatId);
        hotel.setName(name);
        HOTEL_DTO_CHAT_CONTAINER.put(chatId, hotel);
    }

    @Override
    public void setAddress(String address, long chatId) {
        var hotel = HOTEL_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set hotel address {}, chatId {}", address, chatId);
        hotel.setAddress(address);
        HOTEL_DTO_CHAT_CONTAINER.put(chatId, hotel);
    }

    @Override
    public void setCheckInDate(LocalDate checkInDate, long chatId) {
        var hotel = HOTEL_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set hotel check-in date {}, chatId {}", checkInDate, chatId);
        if (nonNull(hotel.getCheckOutDate()) && hotel.getCheckOutDate().isBefore(checkInDate)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        hotel.setCheckInDate(checkInDate);
        HOTEL_DTO_CHAT_CONTAINER.put(chatId, hotel);
    }

    @Override
    public void setCheckOutDate(LocalDate checkOutDate, long chatId) {
        var hotel = HOTEL_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set hotel check-out date {}, chatId {}", checkOutDate, chatId);
        if (nonNull(hotel.getCheckInDate()) && hotel.getCheckInDate().isAfter(checkOutDate)) {
            throw BusinessException.builder(ResponseCode.INVALID_TIMELINE)
                    .build();
        }
        hotel.setCheckOutDate(checkOutDate);
        HOTEL_DTO_CHAT_CONTAINER.put(chatId, hotel);
    }

    @Override
    public Collection<HotelDto> getHotelsByTripId(long tripId) {
        log.debug("Get hotels by tripId {}", tripId);
        var hotels = hotelCoreClient.getHotelsByTrip(tripId);
        log.info("{} hotels obtained by tripId {}", hotels.size(), tripId);
        return hotels;
    }

    @Override
    public HotelDto getHotelById(long id) {
        log.debug("Get hotel by id {}", id);
        var hotel = hotelCoreClient.getHotelById(id);
        log.info("Hotel obtained by id {}", id);
        return hotel;
    }

    @Override
    public void deleteHotel(long id) {
        log.debug("Delete hotel by id {}", id);
        hotelCoreClient.deleteHotel(id);
        log.info("Hotel deleted by id {}", id);
    }

    public HotelDto commitNewHotel(long chatId) {
        log.debug("Commit hotel for chatId {}", chatId);
        var hotel = HOTEL_DTO_CHAT_CONTAINER.get(chatId);
        var savedHotel = hotelCoreClient.createHotel(hotel);
        HOTEL_DTO_CHAT_CONTAINER.remove(chatId);

        log.info("Hotel {} for chatId {} commited", hotel, chatId);
        return savedHotel;
    }
}
