package com.planyourtrip.bot.service.core;

import com.planyourtrip.bot.config.http.FeignConfig;
import com.planyourtrip.bot.dto.domain.AccommodationDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(name = "core-service-accommodation-client",
        url = "${core-service.rest-base-url}/api/accommodation",
        configuration = FeignConfig.class)
public interface AccommodationCoreClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    AccommodationDto createAccommodation(@RequestBody AccommodationDto accommodationDto);

    @RequestLine("GET /trip/{tripId}")
    Collection<AccommodationDto> getAccommodationsByTrip(@Param Long tripId);

    @RequestLine("DELETE /{id}")
    Void deleteAccommodation(@Param Long id);

    @RequestLine("GET /{id}")
    AccommodationDto getAccommodationById(@Param Long id);

    @RequestLine("PUT /{id}")
    @Headers("Content-Type: application/json")
    AccommodationDto updateAccommodation(@Param Long id, @RequestBody AccommodationDto accommodationDto);
}
