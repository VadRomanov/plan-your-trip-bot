package com.planyourtrip.bot.service.core;

import com.planyourtrip.bot.config.http.FeignConfig;
import com.planyourtrip.bot.dto.domain.TicketDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(name = "core-service-ticket-client",
        url = "${core-service.rest-base-url}/api/ticket",
        configuration = FeignConfig.class)
public interface TicketCoreClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    TicketDto createTicket(@RequestBody TicketDto ticketDto);

    @RequestLine("GET /trip/{tripId}")
    Collection<TicketDto> getTicketsByTrip(@Param Long tripId);

    @RequestLine("DELETE /{id}")
    Void deleteTicket(@Param Long id);

    @RequestLine("GET /{id}")
    TicketDto getTicketById(@Param Long id);

    @RequestLine("PUT /{id}")
    TicketDto updateTicket(@Param Long id, @RequestBody TicketDto ticketDto);
}
