package com.planyourtrip.bot.dto.domain;

import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.utils.TextUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static java.util.Objects.nonNull;

@Data
@Accessors(chain = true)
@Builder
public class AccommodationDto {
    private Long id;
    @NonNull
    private Long tripId;
    @NonNull
    private AccommodationType type;
    @NonNull
    private String name;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String address;
    private String fileUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Override
    public String toString() {
        return String.format("%s %s%s%s", type, name,
                nonNull(address) ? String.format(" (%s)", address) : Strings.EMPTY,
                TextUtils.getRange(checkInDate, checkOutDate, BotAnswer.FROM, BotAnswer.TILL));
    }
}
