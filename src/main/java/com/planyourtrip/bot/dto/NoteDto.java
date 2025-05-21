package com.planyourtrip.bot.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class NoteDto {
    private Long id;
    @NonNull
    private Long tripId;
    @NonNull
    private String content;
    private OffsetDateTime createdAt;
}
