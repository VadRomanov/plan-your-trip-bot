package com.planyourtrip.bot.service.core;

import com.planyourtrip.bot.config.http.FeignConfig;
import com.planyourtrip.bot.dto.domain.TripDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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
    TripDto updateTrip(@Param Long id, @RequestBody TripDto tripDto);

    @RequestLine("GET /user/{telegramId}")
    Collection<TripDto> getTripsByUser(@Param Long telegramId);

    @RequestLine("GET /{id}/summary")
    ResponseEntity<?> getTripSummary(@Param Long id, @RequestHeader(HttpHeaders.ACCEPT) String acceptHeader);
}
