package com.markbolo.event.consumer;

public class ConsumerException extends RuntimeException {

    public ConsumerException(Exception e) {
        super(e);
    }

    public ConsumerException(String message) {
        super(message);
    }

    public ConsumerException(String message, Throwable e) {
        super(message, e);
    }
}
