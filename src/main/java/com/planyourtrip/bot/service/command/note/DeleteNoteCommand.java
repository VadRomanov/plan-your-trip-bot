package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.impl.AbstractDeleteCommand;
import com.planyourtrip.bot.service.command.note.util.NoteUtil;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteNoteCommand extends AbstractDeleteCommand {
    private final NoteService noteService;

    @Override
    protected ResponseDto requestEntityId(long tripId) {
        var notes = noteService.getNotesByTripId(tripId);
        return ResponseDto.builder()
                .text(notes.isEmpty() ? BotAnswer.MY_NOTES_EMPTY_RESPONSE : BotAnswer.CHOOSE_NOTE_REQUEST)
                .keyboard(notes.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_NOTE)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(NoteUtil.mapNotesToMap(notes), tripId,
                        getCommandType()))
                .build();
    }

    @Override
    protected ResponseDto requestConfirmation(long id) {
        var note = noteService.getNoteById(id);
        var text = String.format(BotAnswer.DELETE_NOTE_CONFIRMATION_REQUEST, note);
        return requestConfirmation(text, note.getTripId(), id);
    }

    @Override
    protected ResponseDto doDelete(long id) {
        noteService.deleteNote(id);
        return ResponseDto.builder()
                .text(BotAnswer.DELETE_NOTE_FINAL_RESPONSE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_NOTE;
    }
}
