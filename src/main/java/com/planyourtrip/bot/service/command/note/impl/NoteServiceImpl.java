package com.planyourtrip.bot.service.command.note.impl;

import com.planyourtrip.bot.dto.NoteDto;
import com.planyourtrip.bot.service.command.note.NoteService;
import com.planyourtrip.bot.service.core.NoteCoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteCoreClient noteCoreClient;

    private static final Map<Long, NoteDto.NoteDtoBuilder> NOTE_DTO_CHAT_CONTAINER = new ConcurrentHashMap<>();
    private static final Map<Long, NoteDto> NOTE_DTO_CHAT_UPDATE_CONTAINER = new ConcurrentHashMap<>();

    @Override
    public void createNote(long tripId, long chatId) {
        log.debug("Create note, chatId {}", chatId);
        NOTE_DTO_CHAT_CONTAINER.put(chatId, NoteDto.builder().tripId(tripId));
    }

    @Override
    public void setTitle(String title, long chatId) {
        var hotelBuilder = NOTE_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set note title {}, chatId {}", title, chatId);
        hotelBuilder.title(title);
        NOTE_DTO_CHAT_CONTAINER.put(chatId, hotelBuilder);
    }

    @Override
    public void setContent(String content, long chatId) {
        var hotelBuilder = NOTE_DTO_CHAT_CONTAINER.get(chatId);
        log.debug("Set note content {}, chatId {}", Strings.left(content, 20), chatId);
        hotelBuilder.content(content);
        NOTE_DTO_CHAT_CONTAINER.put(chatId, hotelBuilder);
    }

    @Override
    public Collection<NoteDto> getNotesByTripId(long tripId) {
        log.debug("Get notes by tripId {}", tripId);
        var notes = noteCoreClient.getNotesByTrip(tripId);
        log.info("{} notes obtained by tripId {}", notes.size(), tripId);
        return notes;
    }

    @Override
    public NoteDto getNoteById(long id) {
        log.debug("Get note by id {}", id);
        var note = noteCoreClient.getNoteById(id);
        log.info("Note obtained by id {}", id);
        return note;
    }

    @Override
    public void fetchNoteById(long id, long chatId) {
        var note = getNoteById(id);
        NOTE_DTO_CHAT_UPDATE_CONTAINER.put(chatId, note);
    }

    @Override
    public void deleteNote(long id) {
        log.debug("Delete note by id {}", id);
        noteCoreClient.deleteNote(id);
        log.info("Note deleted by id {}", id);
    }

    @Override
    public NoteDto commitNewNote(long chatId) {
        log.debug("Commit note for chatId {}", chatId);
        var noteBuilder = NOTE_DTO_CHAT_CONTAINER.get(chatId);
        var savedNote = noteCoreClient.createNote(noteBuilder.build());
        NOTE_DTO_CHAT_CONTAINER.remove(chatId);

        log.info("Note {} for chatId {} commited", savedNote, chatId);
        return savedNote;
    }

    @Override
    public NoteDto getNoteToUpdate(long chatId) {
        return NOTE_DTO_CHAT_UPDATE_CONTAINER.get(chatId);
    }

    @Override
    public void updateNote(NoteDto note, long chatId) {
        log.debug("Update note {}", note);
        var savedNote = noteCoreClient.updateNote(note.getId(), note);
        NOTE_DTO_CHAT_UPDATE_CONTAINER.remove(chatId);
        log.info("Note {} updated", savedNote);
    }
}
