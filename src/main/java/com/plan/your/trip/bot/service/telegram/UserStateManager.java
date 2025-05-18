package com.plan.your.trip.bot.service.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStateManager {
    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> tempData = new ConcurrentHashMap<>();

    public void setState(long userId, UserState state) {
        userStates.put(userId, state);
    }

    public UserState getState(long userId) {
        return userStates.get(userId);
    }

    public void clearState(long userId) {
        userStates.remove(userId);
        tempData.remove(userId);
    }

    public void saveTempData(long userId, String key, String value) {
        tempData.computeIfAbsent(userId, k -> new HashMap<>()).put(key, value);
    }

    public String getTempData(long userId, String key) {
        return tempData.getOrDefault(userId, Collections.emptyMap()).get(key);
    }

    public Map<String, String> getAllTempData(long userId) {
        return tempData.getOrDefault(userId, Collections.emptyMap());
    }

    @Data
    @AllArgsConstructor
    public static class UserState {
        private String responsibleCommand;
        private String state;
        private Map<String, Object> stateEntities;
    }
}
