package com.planyourtrip.bot.service.mapper;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.state.UserState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class TelegramMapper {

    public CommandDto toCommandDto(Message message) {
        var commandName = CommandType.valueOf(message.getText().substring(1).toUpperCase());
        return createCommandDto(commandName, message);
    }

    public MessageDto toResponseMessageDto(Message message, UserState state) {
        return MessageDto.builder()
                .commandType(state.getResponsibleCommand())
                .telegramId(message.getFrom().getId())
                .chatId(message.getChatId())
                .userName(message.getFrom().getUserName())
                .msgText(message.getText().trim())
                .document(message.getDocument())
                .state(state)
                .messageId(message.getMessageId())
                .firstName(message.getFrom().getFirstName())
                .lastName(message.getFrom().getLastName())
                .languageCode(message.getFrom().getLanguageCode())
                .build();
    }

    public CallbackDto toCallbackDto(CallbackQuery callback) {
        var callbackData = callback.getData().split("/");
        var commandType = CommandType.valueOf(callbackData[0].toUpperCase());
        return CallbackDto.builder()
                .commandType(commandType)
                .telegramId(callback.getFrom().getId())
                .chatId(callback.getMessage().getChatId())
                .userName(callback.getFrom().getUserName())
                .callbackData(callbackData)
                .messageId(callback.getMessage().getMessageId())
                .languageCode(callback.getFrom().getLanguageCode())
                .firstName(callback.getFrom().getFirstName())
                .lastName(callback.getFrom().getLastName())
                .build();
    }

  public CommandDto toDefaultCommandDto(CallbackQuery callbackQuery) {
    return CommandDto.builder()
        .commandType(CommandType.DEFAULT)
        .telegramId(callbackQuery.getFrom().getId())
        .chatId(callbackQuery.getMessage().getChatId())
        .build();
  }

    public CommandDto toDefaultCommandDto(Message message) {
        return createCommandDto(CommandType.DEFAULT, message);
    }

    private CommandDto createCommandDto(CommandType commandType, Message message) {
        return CommandDto.builder()
                .commandType(commandType)
                .telegramId(message.getFrom().getId())
                .chatId(message.getChatId())
                .userName(message.getFrom().getUserName())
                .languageCode(message.getFrom().getLanguageCode())
                .firstName(message.getFrom().getFirstName())
                .lastName(message.getFrom().getLastName())
                .build();
    }

}
