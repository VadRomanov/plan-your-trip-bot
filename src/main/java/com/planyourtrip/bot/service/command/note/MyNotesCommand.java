package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.dto.domain.NoteDto;
import com.planyourtrip.bot.service.command.impl.AbstractMyCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyNotesCommand extends AbstractMyCommand {
    private final NoteService noteService;

    @Override
    protected ResponseDto prepareAnswer(long tripId) {
        var notes = noteService.getNotesByTripId(tripId);
        if (notes.isEmpty()) {
            return ResponseDto.builder()
                    .text(BotAnswer.MY_NOTES_EMPTY_RESPONSE)
                    .keyboard(getAddKeyboard(tripId, CommandType.ADD_NOTE))
                    .build();
        }
        return ResponseDto.builder()
                .text(String.format(BotAnswer.MY_NOTES_RESPONSE,
                        String.join(",",
                                notes.stream()
                                        .map(NoteDto::toString)
                                        .toList())))
                .keyboard(getActionsKeyboard(tripId, CommandType.EDIT_NOTE, CommandType.DELETE_NOTE))
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_NOTES;
    }
}
