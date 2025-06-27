package com.planyourtrip.bot.service.core;

import com.planyourtrip.bot.config.http.FeignConfig;
import com.planyourtrip.bot.dto.HotelDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(name = "core-service-hotel-client",
        url = "${core-service.rest-base-url}/api/hotel",
        configuration = FeignConfig.class)
public interface HotelCoreClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    HotelDto createHotel(@RequestBody HotelDto hotelDto);

    @RequestLine("GET /trip/{tripId}")
    Collection<HotelDto> getHotelsByTrip(@Param Long tripId);

    @RequestLine("DELETE /{id}")
    Void deleteHotel(@Param Long id);

    @RequestLine("GET /{id}")
    HotelDto getHotelById(@Param Long id);

    @RequestLine("PUT /{id}")
    HotelDto updateHotel(@Param Long id, @RequestBody HotelDto hotelDto);
}
