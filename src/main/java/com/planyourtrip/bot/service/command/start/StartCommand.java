package com.planyourtrip.bot.service.command.start;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.planyourtrip.bot.constant.CommandType.HELP;
import static com.planyourtrip.bot.constant.CommandType.MY_TRIPS;
import static com.planyourtrip.bot.constant.CommandType.NEW_TRIP;

@RequiredArgsConstructor
@Component
public class StartCommand extends AbstractCommand {

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        var keyboard = ReplyKeyboardBuilder.buildInlineKeyboard(List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        NEW_TRIP.getDescription(), NEW_TRIP.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(
                        MY_TRIPS.getDescription(), MY_TRIPS.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(
                        HELP.getDescription(), HELP.getName())
        ));

        return ResponseDto.builder()
                .text(BotAnswer.START_ANSWER)
                .chatId(commandDto.getChatId())
                .keyboard(keyboard)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.START;
    }
}
