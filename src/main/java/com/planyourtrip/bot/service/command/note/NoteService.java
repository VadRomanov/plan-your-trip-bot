package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.dto.NoteDto;

import java.util.Collection;

public interface NoteService {

    void createNote(long tripId, long chatId);

    void setTitle(String title, long chatId);

    void setContent(String content, long chatId);

    Collection<NoteDto> getNotesByTripId(long tripId);

    NoteDto getNoteById(long id);

    void fetchNoteById(long id, long chatId);

    void deleteNote(long id);

    NoteDto commitNewNote(long chatId);

    NoteDto getNoteToUpdate(long chatId);

    void updateNote(NoteDto note, long chatId);
}
