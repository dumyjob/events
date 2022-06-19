package com.markbolo.event;

import com.markbolo.event.storage.Event;
import com.markbolo.event.storage.EventStorage;

/**
 * USAGE:
 * <p>
 * EventProducer.createInstance()
 * <p>
 * EventProducer.instance()
 * .publish(final T domainEvent);
 */
public class EventPublisher {

    private static EventPublisher instance;
    private final EventStorage eventStorage;

    public EventPublisher(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public static void createInstance(EventStorage eventStorage) {
        instance = new EventPublisher(eventStorage);
    }

    public static EventPublisher instance() {
        return instance;
    }

    public void produce(final String topic, final String tag, final Object message) {
        Event event = new Event(topic, tag, message);
        eventStorage.store(event);
    }

    public void produce(final com.markbolo.event.Event event) {
        com.markbolo.event.storage.Event domainEvent = new Event(event.topic(), event.tag(), event);
        eventStorage.store(domainEvent);
    }
}
