package com.planyourtrip.bot.service.core;

import com.planyourtrip.bot.config.http.FeignConfig;
import com.planyourtrip.bot.dto.domain.NoteDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(name = "core-service-note-client",
        url = "${core-service.rest-base-url}/api/note",
        configuration = FeignConfig.class)
public interface NoteCoreClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    NoteDto createNote(@RequestBody NoteDto noteDto);

    @RequestLine("GET /trip/{tripId}")
    Collection<NoteDto> getNotesByTrip(@Param Long tripId);

    @RequestLine("DELETE /{id}")
    Void deleteNote(@Param Long id);

    @RequestLine("GET /{id}")
    NoteDto getNoteById(@Param Long id);

    @RequestLine("PUT /{id}")
    @Headers("Content-Type: application/json")
    NoteDto updateNote(@Param Long id, @RequestBody NoteDto noteDto);
}
