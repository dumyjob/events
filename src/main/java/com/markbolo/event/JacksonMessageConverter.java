package com.markbolo.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonMessageConverter implements MessageConverter {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    @Override
    public String format(Object message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }
}
