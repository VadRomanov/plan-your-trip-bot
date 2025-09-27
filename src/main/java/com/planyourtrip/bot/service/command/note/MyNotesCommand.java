package com.planyourtrip.bot.service.command.note;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.NoteDto;
import com.planyourtrip.bot.dto.TripDto;
import com.planyourtrip.bot.service.command.impl.AbstractCommand;
import com.planyourtrip.bot.service.command.trip.impl.TripServiceImpl;
import com.planyourtrip.bot.service.dto.CallbackDto;
import com.planyourtrip.bot.service.dto.CommandDto;
import com.planyourtrip.bot.service.dto.ResponseDto;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyNotesCommand extends AbstractCommand {
    private final TripServiceImpl tripService;
    private final NoteService noteService;

    @Override
    public ResponseDto processCommand(CommandDto commandDto) {
        return processInitResponse(commandDto.getTelegramId());
    }

    @Override
    public ResponseDto processCallback(CallbackDto callbackDto) {
        var step = callbackDto.getCallbackData().length;
        if (step == 1) {
            return processInitResponse(callbackDto.getTelegramId());
        } else if (step == 2) {
            return prepareAnswer(Long.parseLong(callbackDto.getCallbackData()[1]));
        } else {
            return returnErrorMessage();
        }
    }

    private ResponseDto processInitResponse(long telegramId) {
        var trips = tripService.getTripsByTelegramId(telegramId);
        return ResponseDto.builder()
                .text(trips.isEmpty() ? BotAnswer.MY_TRIPS_EMPTY_RESPONSE : BotAnswer.CHOOSE_TRIP_RESPONSE)
                .keyboard(trips.isEmpty() ? getNewTripKeyboard() : getTripsKeyboard(trips))
                .build();
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getActionsKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.EDIT_NOTE.getDescription(),
                        String.format("%s/%s", CommandType.EDIT_NOTE.getName(), tripId)),
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.DELETE_NOTE.getDescription(),
                        String.format("%s/%s", CommandType.DELETE_NOTE.getName(), tripId))
        );
    }

    private ResponseDto prepareAnswer(long tripId) {
        var notes = noteService.getNotesByTripId(tripId);
        if (notes.isEmpty()) {
            return ResponseDto.builder()
                    .text(BotAnswer.MY_NOTES_EMPTY_RESPONSE)
                    .keyboard(getAddNoteKeyboard(tripId))
                    .build();
        }
        return ResponseDto.builder()
                .text(String.format("""
                                <b>Список заметок:
                                %s</b>
                                """,
                        String.join(",",
                                notes.stream()
                                        .map(NoteDto::toString)
                                        .toList())))
                .keyboard(getActionsKeyboard(tripId))
                .build();
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getTripsKeyboard(Collection<TripDto> trips) {
        return ReplyKeyboardBuilder.buildTripsButtons(trips, CommandType.MY_NOTES);
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getAddNoteKeyboard(long tripId) {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.ADD_NOTE.getDescription(),
                        format("%s/%s", CommandType.ADD_NOTE.getName(), tripId))
        );
    }

    private List<ReplyKeyboardBuilder.KeyboardButton> getNewTripKeyboard() {
        return List.of(
                new ReplyKeyboardBuilder.KeyboardButton(
                        CommandType.NEW_TRIP.getDescription(),
                        CommandType.NEW_TRIP.getName())
        );
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_NOTES;
    }
}
