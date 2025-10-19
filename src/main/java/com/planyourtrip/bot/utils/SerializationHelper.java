package com.planyourtrip.bot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class SerializationHelper {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String objectToString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    public <T> T canDeserializeAndGet(HttpEntity httpEntity, Class<T> clazz) {
        try (var inputStream = httpEntity.getContent()) {
            return canDeserializeAndGet(inputStream, clazz);
        } catch (Exception e) {
            log.error("Cannot deserialize input stream to type {}", clazz, e);
            return null;
        }
    }

    @SneakyThrows
    public <T> T canDeserializeAndGet(byte[] bytes, Class<T> clazz) {
        log.trace("try to deserialize bytes to {}", clazz.getName());
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            log.error("Cannot deserialize bytes to type {}", clazz, e);
            return null;
        }
    }

    /**
     * Returns deserialized object or null if cannot deserialize
     *
     * @param inputStream input stream that need to deserialize to java class
     * @param clazz       value type
     * @param <T>         type parameter
     * @return deserialized object or null
     */
    public <T> T canDeserializeAndGet(InputStream inputStream, Class<T> clazz) {
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (Exception e) {
            log.error("Cannot deserialize input stream to type {}", clazz, e);
            return null;
        }
    }
}
