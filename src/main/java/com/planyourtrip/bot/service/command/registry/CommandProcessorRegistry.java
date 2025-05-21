package com.planyourtrip.bot.service.command.registry;

import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class CommandProcessorRegistry {
    private static final Map<CommandType, AbstractCommand> PROCESSORS = new HashMap<>();

    public void register(final CommandType commandType,
                         final AbstractCommand commandProcessor) {
        if (nonNull(commandType) && nonNull(commandProcessor)) {
            if (PROCESSORS.containsKey(commandType)) {
                logErrorAndExit("error! overriding key inside CommandProcessorRegistry!");
            }
            PROCESSORS.put(commandType, commandProcessor);
        } else {
            logErrorAndExit(
                    String.format("error! name or commandProcessor is missing! Name %s. Processor %s", commandType,
                            commandProcessor));
        }
    }

    public AbstractCommand getProcessor(final CommandType commandType) {
        var commandProcessor = PROCESSORS.get(commandType);
        if (isNull(commandProcessor)) {
            log.warn("Command processor not found by type {}", commandType.getName());
            commandProcessor = PROCESSORS.get(CommandType.DEFAULT);
        }

        return commandProcessor;
    }

    private void logErrorAndExit(String text) {
        log.error(text);
        System.exit(1);
    }
}
