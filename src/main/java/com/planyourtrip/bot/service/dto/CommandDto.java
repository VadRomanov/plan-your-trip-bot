package com.planyourtrip.bot.service.dto;

import com.planyourtrip.bot.constant.CommandType;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommandDto implements AbstractRequestDto {
    @NonNull
    private CommandType commandType;
    @NonNull
    private Long userId;
    @NonNull
    private Long chatId;
}