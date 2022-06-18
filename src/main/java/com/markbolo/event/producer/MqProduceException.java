package com.markbolo.event.producer;

public class MqProduceException  extends RuntimeException {

    public MqProduceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqProduceException(InterruptedException e) {
        super(e);
    }
}
