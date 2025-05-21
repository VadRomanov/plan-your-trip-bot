package com.planyourtrip.bot.service.dto;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.state.UserState;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageDto implements AbstractRequestDto {
    @NonNull
    private CommandType commandType;
    @NonNull
    private Long userId;
    @NonNull
    private Long chatId;
    @NonNull
    private String msgText;
    @NonNull
    private UserState state;
    @NonNull
    private Integer messageId;
}
