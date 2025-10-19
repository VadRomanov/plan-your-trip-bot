package com.planyourtrip.bot.dto.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Accessors(chain = true)
public class TripSummaryDto {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean expired;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Set<TicketDto> tickets;
    private Set<AccommodationDto> accommodations;
    private Set<NoteDto> notes;
    @NonNull
    private Set<UserDto> users;
}
