package com.planyourtrip.bot.dto.domain;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.utils.TextUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Builder
public class TicketDto {
    private Long id;
    @NonNull
    private Long tripId;
    @NonNull
    private TicketType type;
    @NonNull
    private String departure;
    @NonNull
    private String arrival;
    private OffsetDateTime departureTime;
    private OffsetDateTime arrivalTime;
    private String fileUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Override
    public String toString() {
        return String.format("%s %s - %s%s", type.getName(), departure, arrival,
                TextUtils.getRange(departureTime, arrivalTime, BotAnswer.START, BotAnswer.END));
    }
}
