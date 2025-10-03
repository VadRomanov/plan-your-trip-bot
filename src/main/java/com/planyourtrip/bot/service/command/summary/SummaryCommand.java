package com.planyourtrip.bot.service.command.summary;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.summary.util.SummaryUtil;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class SummaryCommand extends AbstractCommand {
    private final SummaryService summaryService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestSummaryFormat(callbackDto);
        } else if (step == 3) {
            return processFormatResponse(callbackDto);
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto requestSummaryFormat(CallbackDto callbackDto) {
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        return ResponseDto.builder()
                .text(BotAnswer.CHOOSE_SUMMARY_FORMAT_REQUEST)
                .keyboard(createFormatsKeyboard(tripId))
                .build();
    }

    private ResponseDto processFormatResponse(CallbackDto callbackDto) {
        return switch (SummaryUtil.Format.valueOf(callbackDto.getCallbackData().get(2))) {
            case PDF -> processPdfResponse(callbackDto);
            case TEXT -> processTextResponse(callbackDto);
        };
    }

    private ResponseDto processPdfResponse(CallbackDto callbackDto) {
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var responseFile = summaryService.getTripSummaryPdf(tripId);
        return ResponseDto.builder()
                .responseFile(responseFile)
                .build();
    }

    private ResponseDto processTextResponse(CallbackDto callbackDto) {
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        var response = summaryService.getTripSummaryText(tripId);
        return ResponseDto.builder()
                .text(response)
                .build();
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> createFormatsKeyboard(long tripId) {
        var buttons = new ArrayList<ReplyKeyboardBuilder.KeyboardButton>();
        for (var format : SummaryUtil.Format.values()) {
            buttons.add(new ReplyKeyboardBuilder.KeyboardButton(format.getValue(),
                    String.format("%s/%s/%s", getCommandType().getName(), tripId, format)));
        }
        return buttons;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.SUMMARY;
    }
}
