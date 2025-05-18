package com.plan.your.trip.bot.service.telegram.dto;

public record CallbackDto(String[] callbackData, long chatId, long userId) {
}
