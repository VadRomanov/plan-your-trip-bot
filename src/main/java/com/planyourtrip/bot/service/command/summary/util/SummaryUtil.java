package com.planyourtrip.bot.service.command.summary.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SummaryUtil {

    @Getter
    @RequiredArgsConstructor
    public enum Format {
        PDF("PDF"),
        TEXT("Текст");

        private final String value;
    }


}
