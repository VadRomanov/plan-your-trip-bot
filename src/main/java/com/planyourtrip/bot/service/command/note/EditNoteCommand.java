package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.CallbackDto;
import com.planyourtrip.bot.dto.MessageDto;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractEditCommand;
import com.planyourtrip.bot.service.command.note.util.NoteUtil;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditNoteCommand extends AbstractEditCommand {
    private final NoteService noteService;

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (NoteUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TITLE -> processTitleResponse(messageDto);
            case AWAIT_CONTENT -> processContentResponse(messageDto);
        };
    }

    @Override
    protected ResponseDto requestEntityId(long tripId) {
        var notes = noteService.getNotesByTripId(tripId);
        return ResponseDto.builder()
                .text(notes.isEmpty() ? BotAnswer.MY_NOTES_EMPTY_RESPONSE : BotAnswer.CHOOSE_NOTE_REQUEST)
                .keyboard(notes.isEmpty()
                        ? ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.ADD_NOTE)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(NoteUtil.mapNotesToMap(notes), tripId,
                        getCommandType()))
                .build();
    }

    @Override
    protected ResponseDto requestNewValue(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        long noteId = Long.parseLong(callbackDto.getCallbackData().get(2));
        var newValue = NoteUtil.State.valueOf(callbackDto.getCallbackData().get(3));
        noteService.fetchNoteById(noteId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(getCommandType())
                .setState(newValue.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_REQUEST)
                .build();
    }

    @Override
    protected List<ReplyKeyboardBuilder.KeyboardButton> createStatesKeyboard(long tripId, long id) {
        var buttons = new ArrayList<ReplyKeyboardBuilder.KeyboardButton>();
        for (var state : NoteUtil.State.values()) {
            buttons.add(new ReplyKeyboardBuilder.KeyboardButton(state.getValue(),
                    String.format("%s/%s/%s/%s", getCommandType().getName(), tripId, id,
                            state)));
        }
        return buttons;
    }

    private ResponseDto processTitleResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var note = noteService.getNoteToUpdate(chatId);
        note.setTitle(messageDto.getMsgText());
        noteService.updateNote(note, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    private ResponseDto processContentResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var note = noteService.getNoteToUpdate(chatId);
        note.setContent(messageDto.getMsgText());
        noteService.updateNote(note, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
                .keyboard(defaultKeyboard())
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_NOTE;
    }

}
