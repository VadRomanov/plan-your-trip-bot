package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.note.util.NoteUtil;
import com.planyourtrip.bot.service.command.trip.TripService;
import com.planyourtrip.bot.service.command.trip.util.TripUtil;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.MessageDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.service.state.UserState;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditNoteCommand extends AbstractCommand {

    private final TripService tripService;
    private final NoteService noteService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return requestTripId(commandDto.getTelegramId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().size();
        if (step == 1) {
            return requestTripId(callbackDto.getTelegramId());
        } else if (step == 2) {
            return requestNoteId(Long.parseLong(callbackDto.getCallbackData().get(1)));
        } else if (step == 3) {
            return processNoteIdResponse(callbackDto);
        } else if (step == 4) {
            return requestNewValue(callbackDto);
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

    private ResponseDto requestTripId(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.EDIT_NOTE))
                .build();
    }

    private ResponseDto requestNoteId(long tripId) {
        var notes = noteService.getNotesByTripId(tripId);
        return ResponseDto.builder()
                .text(notes.isEmpty() ? BotAnswer.MY_NOTES_EMPTY_RESPONSE : BotAnswer.CHOOSE_NOTE_REQUEST)
                .keyboard(notes.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.ADD_NOTE)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(NoteUtil.mapNotesToMap(notes), tripId,
                        CommandType.EDIT_NOTE))
                .build();
    }

    private ResponseDto processNoteIdResponse(CallbackDto callbackDto) {
        long tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        long noteId = Long.parseLong(callbackDto.getCallbackData().get(2));
        return ResponseDto.builder()
                .text(BotAnswer.EDIT_CHOOSE_FIELD_REQUEST)
                .keyboard(List.of(
                        new ReplyKeyboardBuilder.KeyboardButton(NoteUtil.State.AWAIT_TITLE.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_NOTE.getName(), tripId, noteId,
                                        NoteUtil.State.AWAIT_TITLE)),
                        new ReplyKeyboardBuilder.KeyboardButton(NoteUtil.State.AWAIT_CONTENT.getValue(),
                                String.format("%s/%s/%s/%s", CommandType.EDIT_NOTE.getName(), tripId, noteId,
                                        NoteUtil.State.AWAIT_CONTENT))))
                .build();
    }

    private ResponseDto requestNewValue(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        long noteId = Long.parseLong(callbackDto.getCallbackData().get(2));
        var newValue = TripUtil.State.valueOf(callbackDto.getCallbackData().get(3));
        noteService.fetchNoteById(noteId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.EDIT_NOTE)
                .setState(newValue.name()));
        return ResponseDto.builder()
                .text(BotAnswer.NEW_VALUE_REQUEST)
                .build();
    }

    private ResponseDto processTitleResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        var note = noteService.getNoteToUpdate(chatId);
        note.setTitle(messageDto.getMsgText());
        noteService.updateNote(note, chatId);
        getUserStateManager().clearState(messageDto.getChatId());
        return ResponseDto.builder()
                .text(BotAnswer.DONE)
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
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.EDIT_NOTE;
    }

}
