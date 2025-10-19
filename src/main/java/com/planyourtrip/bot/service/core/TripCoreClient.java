package com.planyourtrip.bot.service.core;

import com.planyourtrip.bot.config.http.FeignConfig;
import com.planyourtrip.bot.dto.domain.TripDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(name = "core-service-trip-client",
        url = "${core-service.rest-base-url}/api/trip",
        configuration = FeignConfig.class)
public interface TripCoreClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    TripDto createTrip(@RequestBody TripDto tripDto);

    @RequestLine("GET /{id}")
    TripDto getTrip(@Param Long id);

    @RequestLine("DELETE /{id}")
    Void deleteTrip(@Param Long id);

    @RequestLine("PUT /{id}")
    @Headers("Content-Type: application/json")
    TripDto updateTrip(@Param Long id, @RequestBody TripDto tripDto);

    @RequestLine("GET /user/{telegramId}")
    Collection<TripDto> getTripsByUser(@Param Long telegramId);

    @RequestLine("GET /{id}/summary")
    @Headers("Accept: {acceptHeader}")
    Response getTripSummary(@Param("id") Long id, @Param("acceptHeader") String acceptHeader);
}
