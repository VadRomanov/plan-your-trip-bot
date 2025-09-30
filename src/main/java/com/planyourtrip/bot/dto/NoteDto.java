package com.planyourtrip.bot.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.util.Strings;

import java.time.OffsetDateTime;
import java.util.Objects;

@Data
@Accessors(chain = true)
@Builder
public class NoteDto {
    private Long id;
    @NonNull
    private Long tripId;
    @NonNull
    private String content;
    private String title;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Override
    public String toString() {
        return Objects.requireNonNullElse(title, Strings.left(content, 20));
    }
}
