package com.planyourtrip.bot.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public final class BusinessException extends RuntimeException {
    public static final String DELIMITER = ", ";
    private final ResponseCode responseCode;
    private final List<Object> params;
    private final Object data;


    BusinessException(final BusinessExceptionBuilder builder) {
        super(joinMessage(builder.getResponseCode(), builder.getMessage(), builder.getParams()), builder.getCause());
        this.responseCode = builder.getResponseCode();
        final var builderParams = builder.getParams();
        this.params = Collections.unmodifiableList(builderParams == null ? Collections.emptyList() : builderParams);
        this.data = builder.getData();
    }

    public static BusinessExceptionBuilder builder(final ResponseCode responseCode) {
        return new BusinessExceptionBuilder(responseCode);
    }

    private static String joinMessage(final ResponseCode responseCode,
                                      final String message,
                                      final List<Object> params) {
        return Stream.of(responseCode.getDescription(params), message)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(DELIMITER));
    }

    @Getter
    public static class BusinessExceptionBuilder {
        private final ResponseCode responseCode;
        private String message;
        private Throwable cause;
        private List<Object> params;
        private Object data;

        BusinessExceptionBuilder(final ResponseCode responseCodeParam) {
            this.responseCode = responseCodeParam;
        }

        public BusinessExceptionBuilder message(final String messageParam) {
            this.message = messageParam;
            return self();
        }

        public BusinessExceptionBuilder cause(final Throwable causeParam) {
            this.cause = causeParam;
            return self();
        }

        public BusinessExceptionBuilder paramMessage(final String param) {
            message(param);
            params(List.of(param));
            return self();
        }

        public BusinessExceptionBuilder params(final List<?> paramsParam) {
            this.params = Collections.unmodifiableList(paramsParam);
            return self();
        }

        public BusinessExceptionBuilder data(final Object dataParam) {
            this.data = dataParam;
            return self();
        }

        public BusinessExceptionBuilder self() {
            return this;
        }

        public BusinessException build() {
            return new BusinessException(this);
        }
    }

}
