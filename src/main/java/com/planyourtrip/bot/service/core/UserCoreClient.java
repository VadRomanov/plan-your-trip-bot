package com.planyourtrip.bot.service.core;

import com.planyourtrip.bot.config.http.FeignConfig;
import com.planyourtrip.bot.dto.domain.UserDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "core-service-user-client",
        url = "${core-service.rest-base-url}/api/user",
        configuration = FeignConfig.class)
public interface UserCoreClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    UserDto createOrUpdateUser(@RequestBody UserDto userDto);

    @RequestLine("GET /{telegramId}")
    UserDto getUserByTelegramId(@Param long telegramId);

    @RequestLine("DELETE /{telegramId}")
    Void deleteUser(@Param("telegramId") long telegramId);

}
