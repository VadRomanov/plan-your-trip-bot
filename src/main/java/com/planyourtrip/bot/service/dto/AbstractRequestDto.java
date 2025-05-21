package com.planyourtrip.bot.service.dto;

import com.planyourtrip.bot.constant.CommandType;

public interface AbstractRequestDto {
    CommandType getCommandType();
    Long getUserId();
    Long getChatId();
}
