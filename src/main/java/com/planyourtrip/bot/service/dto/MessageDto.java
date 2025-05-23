package com.planyourtrip.bot.service.dto;

import com.planyourtrip.bot.service.state.UserState;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
public class MessageDto extends AbstractRequestDto {
    @NonNull
    private String msgText;
    @NonNull
    private UserState state;
    @NonNull
    private Integer messageId;
}
