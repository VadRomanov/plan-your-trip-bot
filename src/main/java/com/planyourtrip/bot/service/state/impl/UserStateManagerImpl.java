package com.planyourtrip.bot.service.state.impl;

import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.service.state.UserStateManager;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStateManagerImpl implements UserStateManager {
    private static final Map<Long, UserState> USER_STATES = new ConcurrentHashMap<>();
    private static final Map<Long, Map<String, String>> TEMP_DATA = new ConcurrentHashMap<>();

    @Override
    public void setState(long userId, UserState state) {
        USER_STATES.put(userId, state);
    }

    @Override
    public UserState getState(long userId) {
        return USER_STATES.get(userId);
    }

    @Override
    public void clearState(long userId) {
        USER_STATES.remove(userId);
        TEMP_DATA.remove(userId);
    }

    @Override
    public void saveTempData(long userId, String key, String value) {
        TEMP_DATA.computeIfAbsent(userId, k -> new HashMap<>()).put(key, value);
    }

    @Override
    public String getTempData(long userId, String key) {
        return TEMP_DATA.getOrDefault(userId, Collections.emptyMap()).get(key);
    }

    @Override
    public Map<String, String> getAllTempData(long userId) {
        return TEMP_DATA.getOrDefault(userId, Collections.emptyMap());
    }
}
