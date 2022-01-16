package com.markbolo.event;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface MessageConverter {

    String format(Object message) throws JsonProcessingException;

    <T> T from(String message, Class<T> clazz) throws IOException;

    <T> T from(byte[] message, Class<T> clazz) throws IOException;
}
