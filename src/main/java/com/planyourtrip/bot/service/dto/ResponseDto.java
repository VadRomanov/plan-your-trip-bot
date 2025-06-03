package com.planyourtrip.bot.service.dto;

import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
@Builder
public class ResponseDto {
    private String text;
    private long chatId;
    private List<ReplyKeyboardBuilder.KeyboardButton> keyboard;
    private Integer replyToMessageId;
    int messageId;
    private boolean needDelete;

}
