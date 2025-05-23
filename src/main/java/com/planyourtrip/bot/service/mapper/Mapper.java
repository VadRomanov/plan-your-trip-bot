package com.planyourtrip.bot.service.mapper;

import com.planyourtrip.bot.dto.UserDto;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public UserDto commandToUserDto(CommandDto commandDto) {
        return new UserDto()
                .setTelegramId(commandDto.getTelegramId())
                .setUsername(commandDto.getUserName())
                .setFirstName(commandDto.getFirstName())
                .setLastName(commandDto.getLastName())
                .setLanguageCode(commandDto.getLanguageCode());
    }

    public UserDto callbackToUserDto(CallbackDto callbackDto) {
        return new UserDto()
                .setTelegramId(callbackDto.getTelegramId())
                .setUsername(callbackDto.getUserName())
                .setFirstName(callbackDto.getFirstName())
                .setLastName(callbackDto.getLastName())
                .setLanguageCode(callbackDto.getLanguageCode());
    }

    public UserDto messageToUserDto(MessageDto messageDto) {
        return new UserDto()
                .setTelegramId(messageDto.getTelegramId())
                .setUsername(messageDto.getUserName())
                .setFirstName(messageDto.getFirstName())
                .setLastName(messageDto.getLastName())
                .setLanguageCode(messageDto.getLanguageCode());
    }
}
