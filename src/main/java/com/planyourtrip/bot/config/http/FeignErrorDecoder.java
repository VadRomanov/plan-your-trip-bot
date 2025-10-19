package com.planyourtrip.bot.config.http;

import com.planyourtrip.bot.exception.BackendApiResponse;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import com.planyourtrip.bot.utils.SerializationHelper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder;
    private final SerializationHelper serializationHelper;

    public FeignErrorDecoder(SerializationHelper serializationHelper) {
        defaultDecoder = new Default();
        this.serializationHelper = serializationHelper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        //todo: проверить работает ли
        if (response.status() == 404) {
            return BusinessException.builder(ResponseCode.ENTITY_NOT_FOUND)
                    .build();
        } else {
            return decodeResponse(methodKey, response);
        }
    }

    private Exception decodeResponse(String methodKey, Response response) {
        try {
            var message = tryDecodeToApiResponse(response);
            if (nonNull(message)) {
                return BusinessException.builder(ResponseCode.INTERNAL_ERROR)
                        .message(message.getMessage())
                        .build();
            } else {
                return defaultDecoder.decode(methodKey, response);
            }
        } catch (IOException e) {
            return defaultDecoder.decode(methodKey, response);
        }
    }

    private BackendApiResponse tryDecodeToApiResponse(Response response) throws IOException {
        if (isNull(response.body())) {
            return null;
        }

        byte[] body;
        try {
            body = Util.toByteArray(response.body().asInputStream());
        } catch (IOException ignored) {
            return null;
        }

        return serializationHelper.canDeserializeAndGet(body, BackendApiResponse.class);
    }
}
