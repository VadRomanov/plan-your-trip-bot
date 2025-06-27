package com.planyourtrip.bot.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.util.Strings;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Builder
public class NoteDto {
    private Long id;
    @NonNull
    private Long tripId;
    @NonNull
    private String content;
    private OffsetDateTime createdAt;

    @Override
    public String toString() {
        return Strings.left(content, 20);
    }
}
