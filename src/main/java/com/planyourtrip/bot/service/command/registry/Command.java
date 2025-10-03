package com.planyourtrip.bot.service.command.registry;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.AbstractRequestDto;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.CommandDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import org.springframework.beans.factory.InitializingBean;

public interface Command extends InitializingBean {

    ResponseDto process(AbstractRequestDto requestDto);

    /**
     * Обработка полученной команды (/command_name).
     *
     * @param commandDto commandDto
     * @return ResponseDto
     */
    ResponseDto processCommand(CommandDto commandDto);

    /**
     * Обработка нажатия на кнопку.
     *
     * @param callbackDto callbackDto
     * @return ResponseDto
     */
    ResponseDto processCallback(CallbackDto callbackDto);

    /**
     * Обработка сообщений пользователя.
     *
     * @param messageDto messageDto
     * @return ResponseDto
     */
    ResponseDto processMessage(MessageDto messageDto);

    /**
     * Получить тип обработчика.
     *
     * @return CommandType
     */
    CommandType getCommandType();
}
