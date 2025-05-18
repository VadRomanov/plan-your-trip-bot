package com.plan.your.trip.bot.service.telegram.commands.registry;

import com.plan.your.trip.bot.service.telegram.commands.impl.CommandProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.plan.your.trip.bot.service.telegram.commands.Constants.DEFAULT_COMMAND_PROCESSOR_NAME;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class CommandProcessorRegistry {
    private static final Map<String, CommandProcessor> PROCESSORS = new HashMap<>();

    public void register(final String name,
                         final CommandProcessor commandProcessor) {
        if (StringUtils.isNoneBlank(name) && nonNull(commandProcessor)) {
            if (PROCESSORS.containsKey(name)) {
                logErrorAndExit("error! overriding key inside CommandProcessorRegistry!");
            }
            PROCESSORS.put(name, commandProcessor);
        } else {
            logErrorAndExit(String.format("error! name or commandProcessor is missing! Name %s. Processor %s", name,
                    commandProcessor));
        }
    }

    public CommandProcessor getProcessor(final String name) {
        var commandProcessor = PROCESSORS.get(name);
        if (isNull(commandProcessor)) {
            log.warn("Command processor not found by name {}", name);
            commandProcessor = PROCESSORS.get(DEFAULT_COMMAND_PROCESSOR_NAME);
        }

        return commandProcessor;
    }

    private void logErrorAndExit(String text) {
        log.error(text);
        System.exit(1);
    }
}
