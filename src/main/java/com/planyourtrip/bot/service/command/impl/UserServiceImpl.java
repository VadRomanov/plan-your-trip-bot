package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.dto.UserDto;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.service.command.UserService;
import com.planyourtrip.bot.service.core.UserCoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserCoreClient userClient;
    private final RetryTemplate retryTemplate;

    @Override
    public UserDto createOrUpdateUser(UserDto userDto) {
        var response = retryTemplate.execute(retryCallback -> {
            log.debug("{} user {}", isNull(userDto.getId()) ? "Create" : "Update", userDto);
            return userClient.createOrUpdateUser(userDto);
        });
        log.debug("User {} {}", isNull(userDto.getId()) ? "created" : "updated", response);
        return response;
    }

    @Override
    public UserDto getUserByTelegramId(long telegramId) {
        UserDto userDto;
        try {
            userDto = retryTemplate.execute(retryCallback -> {
                log.debug("Get user by telegramId {}", telegramId);
                return userClient.getUserByTelegramId(telegramId);
            });
            log.info("User obtained by telegramId {}", userDto);
        } catch (BusinessException e) {
            log.warn("User not found by telegramId {}", telegramId);
            return null;
        }
        return userDto;
    }

    @Override
    public void deleteUser(long telegramId) {
        retryTemplate.execute(retryCallback -> {
            log.debug("Delete user by telegramId {}", telegramId);
            return userClient.deleteUser(telegramId);
        });
        log.info("User deleted by telegramId {}", telegramId);
    }
}
