package com.planyourtrip.bot.service.command.start;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.UserService;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.mapper.Mapper;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.planyourtrip.bot.constant.CommandType.HELP;
import static com.planyourtrip.bot.constant.CommandType.MY_TRIPS;
import static com.planyourtrip.bot.constant.CommandType.NEW_TRIP;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class StartCommand extends AbstractCommand {
    private final UserService userService;
    private final Mapper mapper;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        var user = userService.getUserByTelegramId(commandDto.getTelegramId());
        if (isNull(user)) {
            userService.createOrUpdateUser(mapper.commandToUserDto(commandDto));
        }

        return ResponseDto.builder()
                .text(String.format(
                        isNull(user) ? BotAnswer.START_ANSWER : BotAnswer.RESTART_ANSWER,
                        Objects.requireNonNullElse(commandDto.getFirstName(), commandDto.getUserName())))
                .keyboard(createReplyKeyboard())
                .build();
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> createReplyKeyboard() {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        NEW_TRIP.getDescription(), NEW_TRIP.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(
                        MY_TRIPS.getDescription(), MY_TRIPS.getName()),
                new ReplyKeyboardBuilder.KeyboardButton(
                        HELP.getDescription(), HELP.getName())
        );
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.START;
    }
}
