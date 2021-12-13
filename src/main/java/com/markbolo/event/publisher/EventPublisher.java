package com.markbolo.event.publisher;

public interface EventPublisher {

    void publish(String topic, String tag, Object message);
}
