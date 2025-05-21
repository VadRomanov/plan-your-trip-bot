package com.planyourtrip.bot.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class TicketDto {
    private Long id;
    @NonNull
    private Long tripId;
    private TicketType type;
    private String departure;
    private String arrival;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String fileUrl;
    private OffsetDateTime createdAt;
}
