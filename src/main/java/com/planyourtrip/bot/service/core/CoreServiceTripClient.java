package com.planyourtrip.bot.service.core;

import com.planyourtrip.bot.config.http.FeignConfig;
import com.planyourtrip.bot.dto.TripDto;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(name = "core-service-trip-client",
        url = "${core-service.rest-base-url}/api/trip",
        configuration = FeignConfig.class)
public interface CoreServiceTripClient {
    @RequestLine("POST")
    TripDto createTrip(@RequestBody TripDto tripDto);
    //         @HeaderMap Map<String, String> headerMap);

    @RequestLine("GET /{id}")
    TripDto getTrip(@PathVariable Long id);

    @RequestLine("DELETE /{id}")
    Void deleteTrip(@PathVariable Long id);

 //   @RequestLine("PUT /{id}")
 //   TripDto updateTrip(@PathVariable Long id, @RequestBody TripDto tripDto);

    @RequestLine("GET /user/{telegramId}")
    Collection<TripDto> getTripsByUser(@PathVariable Long telegramId);


    //   @RequestLine("POST /auth/subsystem/login")
    //   @Headers("Content-Type: application/json")
    //   AccessTokenResponse subsystemLogin(MessagesSubsystemLogin request);

}
