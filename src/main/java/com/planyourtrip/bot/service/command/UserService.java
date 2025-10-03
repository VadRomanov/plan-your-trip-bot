package com.planyourtrip.bot.service.command;

import com.planyourtrip.bot.dto.domain.UserDto;

public interface UserService {

    void createOrUpdateUser(UserDto userDto);

    UserDto getUserByTelegramId(long telegramId);

    void deleteUser(long telegramId);

}
