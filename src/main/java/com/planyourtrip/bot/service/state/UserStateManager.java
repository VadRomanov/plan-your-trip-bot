package com.planyourtrip.bot.service.state;

import java.util.Map;

public interface UserStateManager {
    void setState(long userId, UserState state);

    UserState getState(long userId);

    void clearState(long userId);

    void saveTempData(long userId, String key, String value);

    String getTempData(long userId, String key);

    Map<String, String> getAllTempData(long userId);
}
