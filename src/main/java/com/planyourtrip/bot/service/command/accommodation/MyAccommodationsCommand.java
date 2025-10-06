package com.planyourtrip.bot.service.command.accommodation;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.dto.domain.AccommodationDto;
import com.planyourtrip.bot.service.command.impl.AbstractMyCommand;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyAccommodationsCommand extends AbstractMyCommand {
    private final AccommodationService accommodationService;

    @Override
    protected ResponseDto prepareAnswer(long tripId) {
        var accommodations = accommodationService.getAccommodationsByTripId(tripId);
        if (accommodations.isEmpty()) {
            return ResponseDto.builder()
                    .text(BotAnswer.MY_ACCOMMODATIONS_EMPTY_RESPONSE)
                    .keyboard(ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.ADD_ACCOMMODATION))
                    .build();
        }
        return ResponseDto.builder()
                .text(String.format(BotAnswer.MY_ACCOMMODATIONS_RESPONSE,
                        String.join(",",
                                accommodations.stream()
                                        .map(AccommodationDto::toString)
                                        .toList())))
                .keyboard(ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.EDIT_ACCOMMODATION,
                        CommandType.DELETE_ACCOMMODATION))
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MY_ACCOMMODATIONS;
    }
}
