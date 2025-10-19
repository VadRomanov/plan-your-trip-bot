package com.planyourtrip.bot.utils;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;

import java.util.Base64;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@UtilityClass
public class TextUtils {
    private static final String RANGE_TEMPLATE = " (%s - %s)";
    private static final String OPEN_RANGE_TEMPLATE = " (%s %s)";

    public String getRange(Object start, Object end, String from, String to) {
        if (isNull(start) && isNull(end)) {
            return Strings.EMPTY;
        } else if (nonNull(start) && nonNull(end)) {
            return String.format(RANGE_TEMPLATE, start, end);
        } else if (isNull(end)) {
            return String.format(OPEN_RANGE_TEMPLATE, from, start);
        } else {
            return String.format(OPEN_RANGE_TEMPLATE, to, end);
        }
    }

    public String decodeBase64String(String encodedString) {
        var decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedString);
        return new String(decodedBytes);
    }
}
