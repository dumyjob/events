package com.markbolo.event.consumer;

public class MqConsumeException extends RuntimeException {

    public MqConsumeException(Exception e) {
        super(e);
    }
}
