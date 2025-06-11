package com.planyourtrip.bot.service.impl;

import com.planyourtrip.bot.service.SendService;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendServiceImpl implements SendService {
    private final TelegramClient telegramClient;

    @Override
    public void sendResponse(ResponseDto responseDto) {
        try {
            var message = SendMessage.builder()
                    .text(responseDto.getText())
                    .chatId(responseDto.getChatId())
                    .replyMarkup(nonNull(responseDto.getKeyboard())
                            ? ReplyKeyboardBuilder.buildInlineKeyboard(responseDto.getKeyboard())
                            : null)
                    .replyToMessageId(responseDto.getReplyToMessageId())
                    .parseMode(ParseMode.HTML)
                    .build();

            log.debug("Send response {}", message);
            telegramClient.execute(message);

            if (responseDto.isNeedDelete()) {
                deleteMessage(responseDto.getChatId(), responseDto.getMessageId());
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void deleteMessage(long chatId, int messageId) {
        try {
            log.debug("Delete message {}", messageId);
            DeleteMessages message = DeleteMessages.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
