package com.planyourtrip.bot.service.impl;

import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.SendService;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendServiceImpl implements SendService {
    private final TelegramClient telegramClient;

    @Override
    public void sendResponse(ResponseDto responseDto) {
        SendMessage message = null;
        SendDocument document = null;
        if (nonNull(responseDto.getText())) {
            message = prepareMessToSend(responseDto);
        }
        if (nonNull(responseDto.getResponseFile())) {
            document = prepareDocToSend(responseDto);
        }
        try {
            log.debug("Send response {}", message);
            if (nonNull(document)) {
                telegramClient.execute(document);
            }
            if (nonNull(message)) {
                telegramClient.execute(message);
            }
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

    private SendDocument prepareDocToSend(ResponseDto responseDto) {
        try (var is = new ByteArrayInputStream(responseDto.getResponseFile().getBody())) {
            var document = new InputFile(is, responseDto.getResponseFile().getFileName());
            return SendDocument.builder()
                    .document(document)
                    .chatId(responseDto.getChatId())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SendMessage prepareMessToSend(ResponseDto responseDto) {
        return SendMessage.builder()
                .text(responseDto.getText())
                .chatId(responseDto.getChatId())
                .replyMarkup(nonNull(responseDto.getKeyboard())
                        ? ReplyKeyboardBuilder.buildInlineKeyboard(responseDto.getKeyboard())
                        : null)
                .replyToMessageId(responseDto.getReplyToMessageId())
                .parseMode(ParseMode.HTML)
                .build();
    }
}
