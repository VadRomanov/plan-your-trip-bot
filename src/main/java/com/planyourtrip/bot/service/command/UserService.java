package com.planyourtrip.bot.service.command;

import com.planyourtrip.bot.dto.UserDto;

public interface UserService {

    UserDto createOrUpdateUser(UserDto userDto);

    UserDto getUserByTelegramId(long telegramId);

    void deleteUser(long telegramId);

}
