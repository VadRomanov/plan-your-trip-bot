package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.note.util.NoteUtil;
import com.planyourtrip.bot.service.command.trip.TripService;
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

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddNoteCommand extends AbstractCommand {
    private final NoteService noteService;
    private final TripService tripService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        var trips = tripService.getTripsByTelegramId(commandDto.getTelegramId());
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_NOTE_REQUEST)
                .keyboard(trips.isEmpty()
                        ? ReplyKeyboardBuilder.buildNewEntityButton(CommandType.NEW_TRIP)
                        : ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.ADD_NOTE))
                .build();
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        long chatId = callbackDto.getChatId();
        var tripId = Long.parseLong(callbackDto.getCallbackData().get(1));
        noteService.createNote(tripId, chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_NOTE)
                .setState(NoteUtil.State.AWAIT_TITLE.name()));
        return ResponseDto.builder()
                .text(BotAnswer.ADD_NOTE_TITLE_REQUEST)
                .build();
    }

    @Override
    public ResponseDto processMessage(MessageDto messageDto) {
        return switch (NoteUtil.State.valueOf(messageDto.getState().getState())) {
            case AWAIT_TITLE -> processTitleResponse(messageDto);
            case AWAIT_CONTENT -> processContentResponse(messageDto);
        };
    }

    private ResponseDto processTitleResponse(MessageDto messageDto) {
        long chatId = messageDto.getChatId();
        noteService.setTitle(messageDto.getMsgText(), chatId);
        getUserStateManager().setState(chatId, new UserState()
                .setResponsibleCommand(CommandType.ADD_NOTE)
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

    private List<ReplyKeyboardBuilder.KeyboardButton> getFinalKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_TRIP.getDescription(),
                        format("%s/%s", CommandType.EDIT_TRIP.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.MY_TRIPS.getDescription(),
                        CommandType.MY_TRIPS.getName())
        );
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADD_NOTE;
    }

}
