package com.plan.your.trip.bot.service.telegram.mapper;

import com.plan.your.trip.bot.service.telegram.UserStateManager;
import com.plan.your.trip.bot.service.telegram.dto.CallbackDto;
import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.plan.your.trip.bot.service.telegram.commands.Constants.DEFAULT_COMMAND_PROCESSOR_NAME;

@Component
public class Mapper {

    public MessageDto toCommandDto(Update update) {
        var message = update.getMessage();
        var commandName = message.getText().substring(1);
        return new MessageDto(commandName, message.getFrom().getId(), message.getChatId(), null, null,
                message.getMessageId());
    }

    public MessageDto responseToCommandDto(Update update, UserStateManager.UserState state) {
        var message = update.getMessage();
        return new MessageDto(state.getResponsibleCommand(), message.getFrom().getId(), message.getChatId(),
                message.getText().trim(), state, message.getMessageId());
    }

    public CallbackDto toCallbackDto(Update update) {
        var callbackData = update.getCallbackQuery().getData().split("_");
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var userId = update.getCallbackQuery().getFrom().getId();
        return new CallbackDto(callbackData, chatId, userId);
    }

    public MessageDto toDefaultCommandDto(Update update) {
        var message = update.getMessage();
        return new MessageDto(DEFAULT_COMMAND_PROCESSOR_NAME, message.getFrom().getId(), message.getChatId(), null,
                null, message.getMessageId());
    }

}
