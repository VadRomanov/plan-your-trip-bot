package com.planyourtrip.bot.service.command;

import com.planyourtrip.bot.service.command.registry.CommandProcessorRegistry;
import com.planyourtrip.bot.service.dto.AbstractRequestDto;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final CommandProcessorRegistry registry;

    public ResponseDto handleEvent(AbstractRequestDto requestDto) {
        var commandProcessor = registry.getProcessor(requestDto.getCommandType());
        log.info("Start process {}. Processor {}", requestDto, commandProcessor.getCommandType());

        var result = commandProcessor.process(requestDto);
        log.info("End process {}", requestDto);

        if (requestDto instanceof CallbackDto) {
            result.setNeedDelete(true);
        }
        return result;
    }


}
