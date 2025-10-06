package com.planyourtrip.bot.service.command.accommodation;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.constant.CommandType;
import com.planyourtrip.bot.dto.ResponseDto;
import com.planyourtrip.bot.service.command.accommodation.util.AccommodationUtil;
import com.planyourtrip.bot.service.command.impl.AbstractDeleteCommand;
import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteAccommodationCommand extends AbstractDeleteCommand {
    private final AccommodationService accommodationService;

    @Override
    protected ResponseDto requestEntityId(long tripId) {
        var accommodations = accommodationService.getAccommodationsByTripId(tripId);
        return ResponseDto.builder()
                .text(accommodations.isEmpty() ? BotAnswer.MY_ACCOMMODATIONS_EMPTY_RESPONSE :
                        BotAnswer.CHOOSE_ACCOMMODATION_REQUEST)
                .keyboard(accommodations.isEmpty()
                        ? ReplyKeyboardBuilder.buildActionToTripButton(tripId, CommandType.ADD_ACCOMMODATION)
                        : ReplyKeyboardBuilder.buildEntitiesButtons(
                        AccommodationUtil.mapAccommodationsToMap(accommodations), tripId,
                        getCommandType()))
                .build();
    }

    @Override
    protected ResponseDto requestConfirmation(long id) {
        var accommodation = accommodationService.getAccommodationById(id);
        var text = String.format(BotAnswer.DELETE_ACCOMMODATION_CONFIRMATION_REQUEST,
                accommodation.getName(), accommodation.getCheckInDate(), accommodation.getCheckOutDate());
        return requestConfirmation(text, accommodation.getTripId(), id);
    }

    @Override
    protected ResponseDto doDelete(long id) {
        accommodationService.deleteAccommodation(id);
        return ResponseDto.builder()
                .text(BotAnswer.DELETE_ACCOMMODATION_FINAL_RESPONSE)
                .build();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.DELETE_ACCOMMODATION;
    }
}
