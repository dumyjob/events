package com.markbolo.event;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageConverter {

    String format(Object message) throws JsonProcessingException;

}
