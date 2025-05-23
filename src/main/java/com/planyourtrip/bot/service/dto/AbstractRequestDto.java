package com.planyourtrip.bot.service.dto;

import com.planyourtrip.bot.constant.CommandType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public abstract class AbstractRequestDto {
    @NonNull
    private CommandType commandType;
    @NonNull
    private Long telegramId;
    @NonNull
    private Long chatId;
    private String userName;
    private String firstName;
    private String lastName;
    private String languageCode;
}
