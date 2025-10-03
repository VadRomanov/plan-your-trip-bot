package com.planyourtrip.bot.utils;

import com.planyourtrip.bot.constant.CommandType;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CallbackDataUtil {
    public CommandType getCommandType(List<String> callbackData) {
        return CommandType.valueOf(callbackData.getFirst().toUpperCase());
    }
}
