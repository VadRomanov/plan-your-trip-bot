package com.planyourtrip.bot.dto.domain;

import com.planyourtrip.bot.constant.BotAnswer;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Accessors(chain = true)
public class TripDto {
    private Long id;
    @NonNull
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean expired;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Set<Long> ticketIds;
    private Set<Long> accommodationIds;
    private Set<Long> noteIds;
    @NonNull
    private Set<Long> userIds;

    @Override
    public String toString() {
        return String.format("%s%s (%s - %s)", name, expired ? BotAnswer.EXPIRED : Strings.EMPTY, startDate, endDate);
    }
}
