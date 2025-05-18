package com.plan.your.trip.bot.service.telegram.commands;

import com.plan.your.trip.bot.service.telegram.dto.CallbackDto;
import com.plan.your.trip.bot.service.telegram.dto.MessageDto;
import org.springframework.beans.factory.InitializingBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface Command extends InitializingBean {

    SendMessage process(MessageDto messageDto);

    SendMessage processCallback(CallbackDto callbackDto);

}
