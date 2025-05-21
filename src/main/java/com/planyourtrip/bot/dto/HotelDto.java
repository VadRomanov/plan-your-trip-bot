package com.planyourtrip.bot.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class HotelDto {
    private Long id;
    @NonNull
    private Long tripId;
    private String name;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String address;
    private OffsetDateTime createdAt;
}
