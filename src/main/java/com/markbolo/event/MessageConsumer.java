package com.markbolo.event;

public interface MessageConsumer {
    void start();

    void close();

    boolean started();

    boolean closed();

    String consumerId();
}
