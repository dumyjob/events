package com.markbolo.event.consumer.adpater;

public interface Restartable {

    void start();

    void close();

    boolean started();

    boolean closed();
}
