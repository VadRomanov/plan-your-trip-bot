package com.planyourtrip.bot.exception;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class BackendApiResponse implements Serializable {
    private int status;
    private String message;
    private Object errors;
}
