package com.planyourtrip.bot.dto.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Accessors(chain = true)
public class UserDto {
    private Long id;
    private Long telegramId;
    private String username;
    private String firstName;
    private String lastName;
    private String languageCode;
    private OffsetDateTime createdAt;
    private Set<Long> tripIds;
}
