package com.planyourtrip.bot.dto;

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
    private Set<Long> ticketIds;
    private Set<Long> hotelIds;
    private Set<Long> noteIds;
    @NonNull
    private Set<Long> userIds;

    @Override
    public String toString() {
        return String.format("""
                <b>%s%s</b>
                Даты: %s - %s
                """, name, expired ? BotAnswer.EXPIRED : Strings.EMPTY, startDate, endDate);
    }
}
