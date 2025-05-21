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
    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> tempData = new ConcurrentHashMap<>();

    @Override
    public void setState(long userId, UserState state) {
        userStates.put(userId, state);
    }

    @Override
    public UserState getState(long userId) {
        return userStates.get(userId);
    }

    @Override
    public void clearState(long userId) {
        userStates.remove(userId);
        tempData.remove(userId);
    }

    @Override
    public void saveTempData(long userId, String key, String value) {
        tempData.computeIfAbsent(userId, k -> new HashMap<>()).put(key, value);
    }

    @Override
    public String getTempData(long userId, String key) {
        return tempData.getOrDefault(userId, Collections.emptyMap()).get(key);
    }

    @Override
    public Map<String, String> getAllTempData(long userId) {
        return tempData.getOrDefault(userId, Collections.emptyMap());
    }
}
