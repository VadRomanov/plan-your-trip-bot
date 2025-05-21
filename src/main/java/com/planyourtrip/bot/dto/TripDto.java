package com.planyourtrip.bot.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

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
}
