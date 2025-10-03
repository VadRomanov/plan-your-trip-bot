package com.planyourtrip.bot.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
public class CallbackDto extends AbstractRequestDto {
    private List<String> callbackData;
    private String resourceId;
    @NonNull
    private Integer messageId;
}
