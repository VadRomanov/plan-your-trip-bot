package com.planyourtrip.bot.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Accessors(chain = true)
@Data
@Builder
public class ResponseDto {
    private String text;
    private long chatId;
    private ReplyKeyboard keyboard;
    private Integer replyToMessageId;
    int messageId;
    private boolean needDelete;

}
