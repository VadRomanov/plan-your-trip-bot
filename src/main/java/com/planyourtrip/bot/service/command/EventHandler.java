package com.planyourtrip.bot.service.command;

import com.planyourtrip.bot.service.command.registry.CommandProcessorRegistry;
import com.planyourtrip.bot.service.dto.AbstractRequestDto;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.state.callback.ReplyKeyboardHistory;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final CommandProcessorRegistry registry;
    private final ReplyKeyboardHistory replyKeyboardHistory;

    public ResponseDto handleEvent(AbstractRequestDto requestDto) {
        var commandProcessor = registry.getProcessor(requestDto.getCommandType());
        log.info("Start process {}. Processor {}", requestDto, commandProcessor.getCommandType());

        var result = commandProcessor.process(requestDto);
        log.info("End process {}", requestDto);

        return prepareResponse(result, requestDto);
    }

    private ResponseDto prepareResponse(ResponseDto responseDto, AbstractRequestDto requestDto) {
        responseDto.setChatId(requestDto.getChatId());
        if (requestDto instanceof CallbackDto) {
            responseDto.setNeedDelete(true);
            responseDto.setMessageId(((CallbackDto) requestDto).getMessageId());
        } else if (requestDto instanceof MessageDto) {
            responseDto.setMessageId(((MessageDto) requestDto).getMessageId());
            responseDto.setReplyToMessageId(((MessageDto) requestDto).getMessageId());
        }

        if (nonNull(responseDto.getKeyboard())) {
            if (!replyKeyboardHistory.isEmpty(responseDto.getChatId())) {
                responseDto.setKeyboard(ReplyKeyboardBuilder.addCancelIfNeeded(responseDto.getKeyboard()));
            }
            replyKeyboardHistory.saveState(responseDto.getChatId(),
                    new ReplyKeyboardHistory.State()
                            .setKeyboard(responseDto.getKeyboard())
                            .setText(responseDto.getText()));
        }
        return responseDto;
    }

}
