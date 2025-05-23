package com.planyourtrip.bot.service.command.impl;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.service.command.registry.Command;
import com.planyourtrip.bot.service.command.registry.CommandProcessorRegistry;
import com.planyourtrip.bot.service.dto.AbstractRequestDto;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.state.impl.UserStateManagerImpl;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

import static com.planyourtrip.bot.constant.CommandType.HELP;
import static com.planyourtrip.bot.constant.CommandType.MY_TRIPS;
import static com.planyourtrip.bot.constant.CommandType.NEW_TRIP;

@Slf4j
public abstract class AbstractCommand implements Command {

    @Autowired
    private CommandProcessorRegistry commandProcessorRegistry;
    @Autowired
    private UserStateManagerImpl userStateManager;

    @Override
    public void afterPropertiesSet() {
        commandProcessorRegistry.register(getCommandType(), this);
    }

    @Override
    public final ResponseDto process(AbstractRequestDto requestDto) {
        return switch (requestDto) {
            case CommandDto commandDto -> processCommand(commandDto);
            case CallbackDto callbackDto -> processCallback(callbackDto);
            case MessageDto messageDto -> processMessage(messageDto);
            default -> throw new IllegalStateException("Unexpected value: " + requestDto);
        };
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        return null;
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return null;
    }

    protected ResponseDto returnErrorMessage(Long chatId, Integer messageId) {
        return returnErrorMessage(BotAnswer.DEFAULT_ANSWER, chatId, messageId);
    }

    protected ResponseDto returnErrorMessage(Long chatId) {
        return returnErrorMessage(BotAnswer.SOMETHING_WRONG, chatId, null);
    }

    protected ResponseDto returnErrorMessage(String message, Long chatId, Integer messageId) {
        return ResponseDto.builder()
                .text(message)
                .chatId(chatId)
                .replyToMessageId(messageId)
                .keyboard(defaultKeyboard())
                .build();
    }

    protected UserStateManagerImpl getUserStateManager() {
        return userStateManager;
    }

    private ReplyKeyboard defaultKeyboard() {
        return ReplyKeyboardBuilder.buildInlineKeyboard(List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        NEW_TRIP.getDescription(), NEW_TRIP.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(
                        MY_TRIPS.getDescription(), MY_TRIPS.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(
                        HELP.getDescription(), HELP.getName())
        ));
    }
}
