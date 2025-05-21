package com.planyourtrip.bot.service.dto;

import com.planyourtrip.bot.constant.CommandType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class CallbackDto implements AbstractRequestDto {
    @NonNull
    private CommandType commandType;
    @NonNull
    private Long userId;
    @NonNull
    private Long chatId;
    private String[] callbackData;
    private String resourceId;
    @NonNull
    private Integer messageId;
}
