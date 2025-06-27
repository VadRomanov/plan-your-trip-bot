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
    private AccommodationType type;
    private String name;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String address;
    private OffsetDateTime createdAt;

    @Override
    public String toString() {
        return String.format("%s %s (%s) %s - %s", type, name, address, checkInDate, checkOutDate);
    }
}
