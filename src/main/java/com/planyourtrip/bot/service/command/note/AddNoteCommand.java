package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractAddCommand;
import com.planyourtrip.bot.service.command.note.util.NoteUtil;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AddNoteCommand extends AbstractAddCommand {
    private final NoteService noteService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            var stepValue = callbackDto.getCallbackData().get(1);
            if (COMPLETE_SKIP_VALUES.contains(stepValue)) {
                return processSkipOrCompleteResponse(callbackDto.getChatId(), stepValue);
            } else {
                return processTripIdResponse(callbackDto.getChatId(), Long.parseLong(stepValue));
            }
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (NoteUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_CONTENT -> processContentResponse(messageDto);
            case AWAIT_TITLE -> processTitleResponse(messageDto);
        };
    }

    private ResponseDto processTripIdResponse(long chatId, long tripId) {
        noteService.createNote(tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(NoteUtil.State.AWAIT_CONTENT.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_NOTE_CONTENT_REQUEST)
                .build();
    }

    private ResponseDto processContentResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        noteService.setContent(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(NoteUtil.State.AWAIT_TITLE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_NOTE_TITLE_REQUEST)
                .keyboard(ReplyKeyboardBuilder.buildCompleteButton(getCommandType()))
                .build();
    }

    private ResponseDto processTitleResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        noteService.setTitle(messageDto.getMsgText(), chatId);
        return prepareFinalResponse(chatId);
    }

    @Override
    protected ResponseDto prepareFinalResponse(long chatId) {
        var note = noteService.commitNewNote(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(BotAnswer.ADD_NOTE_FINAL_RESPONSE)
                .keyboard(getFinalKeyboard(note.getTripId()))
                .build();
    }

    @Override
    protected ResponseDto processSkip(long chatId) {
        return null; // not implemented
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_NOTE;
    }

}
