package com.planyourtrip.bot.service.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
public class CallbackDto extends AbstractRequestDto {
    private String[] callbackData;
    private String resourceId;
    @NonNull
    private Integer messageId;
}
