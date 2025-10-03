package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.note.util.NoteUtil;
import com.planyourtrip.bot.service.state.UserState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AddNoteCommand extends AbstractCommand {
    private final NoteService noteService;

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return processTripIdResponse(callbackDto.getChatId(), Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else {
            return returnErrorMessage();
        }
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (NoteUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TITLE -> processTitleResponse(messageDto);
            case AWAIT_CONTENT -> processContentResponse(messageDto);
        };
    }

    private ResponseDto processTripIdResponse(long chatId, long tripId) {
        noteService.createNote(tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(NoteUtil.State.AWAIT_TITLE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_NOTE_TITLE_REQUEST)
                .build();
    }

    private ResponseDto processTitleResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        noteService.setTitle(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(NoteUtil.State.AWAIT_TITLE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_NOTE_CONTENT_REQUEST)
                .build();
    }

    private ResponseDto processContentResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        noteService.setContent(messageDto.getMsgText(), chatId);
        var note = noteService.commitNewNote(chatId);
        getUserStateManager().clearState(chatId);
        return ResponseDto.builder()
                .text(BotAnswer.ADD_NOTE_FINAL_RESPONSE)
                .keyboard(getFinalKeyboard(note.getTripId()))
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_NOTE;
    }

}
