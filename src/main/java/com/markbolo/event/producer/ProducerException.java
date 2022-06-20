package com.markbolo.event.producer;

public class ProducerException extends RuntimeException {

    public ProducerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProducerException(Throwable e) {
        super(e);
    }
}
