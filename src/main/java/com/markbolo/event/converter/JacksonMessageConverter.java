package com.markbolo.event.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonMessageConverter implements MessageConverter {

    private static final ObjectMapper objectMapper;

    static {
        // 默认utf-8格式
        objectMapper = new ObjectMapper();
    }

    @Override
    public String format(Object message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }

    @Override
    public <T> T from(String message, Class<T> clazz) throws IOException {
        return objectMapper.readValue(message, clazz);
    }

    @Override
    public <T> T from(byte[] message, Class<T> clazz) throws IOException {
        return objectMapper.readValue(message, clazz);
    }
}
