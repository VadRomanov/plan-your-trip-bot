package com.planyourtrip.bot.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Builder
public class ResponseFileDto {
    private String fileName;
    private byte[] body;
}
