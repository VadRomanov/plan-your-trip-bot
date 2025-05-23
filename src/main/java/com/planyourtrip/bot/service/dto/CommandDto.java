package com.planyourtrip.bot.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
public class CommandDto extends AbstractRequestDto {
}