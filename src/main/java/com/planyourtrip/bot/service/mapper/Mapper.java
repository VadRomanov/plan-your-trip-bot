package com.planyourtrip.bot.service.mapper;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.state.UserState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class Mapper {

    public CommandDto toCommandDto(Message message) {
        var commandName = CommandType.valueOf(message.getText().substring(1).toUpperCase());
        return new CommandDto(commandName, message.getFrom().getId(), message.getChatId());
    }

    public MessageDto toResponseMessageDto(Message message, UserState state) {
        return new MessageDto(state.getResponsibleCommand(), message.getFrom().getId(), message.getChatId(),
                message.getText().trim(), state, message.getMessageId());
    }

    public CallbackDto toCallbackDto(CallbackQuery callback) {
        var callbackData = callback.getData().split("/");
        var commandType = CommandType.valueOf(callbackData[0].toUpperCase());
        var message = callback.getMessage();
        var chatId = callback.getMessage().getChatId();
        var userId = callback.getFrom().getId();
        return new CallbackDto(commandType, userId, chatId, callbackData, null, message.getMessageId());
    }

    public CommandDto toDefaultCommandDto(Message message) {
        return new CommandDto(CommandType.DEFAULT, message.getFrom().getId(), message.getChatId());
    }

    public CommandDto toDefaultCommandDto(Update update) {
        return toDefaultCommandDto(update.getMessage());
    }

}
