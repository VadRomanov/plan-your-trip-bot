package com.planyourtrip.bot.service.mapper;

import com.planyourtrip.bot.dto.UserDto;
import com.planyourtrip.bot.service.dto.AbstractRequestDto;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public UserDto requestToUserDto(AbstractRequestDto requestDto) {
        return new UserDto()
                .setTelegramId(requestDto.getTelegramId())
                .setUsername(requestDto.getUserName())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setLanguageCode(requestDto.getLanguageCode());
    }
}
