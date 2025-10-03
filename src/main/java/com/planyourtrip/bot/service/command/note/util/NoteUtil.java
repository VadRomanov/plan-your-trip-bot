package com.planyourtrip.bot.service.command.note.util;

import com.planyourtrip.bot.dto.domain.NoteDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class NoteUtil {

    public Map<Long, String> mapNotesToMap(Collection<NoteDto> notes) {
        return notes.stream()
                .collect(Collectors.toMap(NoteDto::getId, NoteDto::toString));
    }

    @Getter
    @RequiredArgsConstructor
    public enum State {
        AWAIT_TITLE("Заголовок"), AWAIT_CONTENT("Содержимое");

        private final String value;
    }

}
