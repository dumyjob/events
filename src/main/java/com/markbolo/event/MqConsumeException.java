package com.markbolo.event;

public class MqConsumeException extends RuntimeException {

    public MqConsumeException(Exception e) {
        super(e);
    }
}
