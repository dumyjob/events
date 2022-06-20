package com.markbolo.event.producer;

public interface EventProducer {

    void publish(String topic, String tag, String body);
}
