package com.plan.your.trip.bot.service.telegram.dto;

import com.plan.your.trip.bot.service.telegram.UserStateManager;

public record MessageDto(String commandName, long userId, long chatId, String msgText, UserStateManager.UserState state, int messageId) {
}
