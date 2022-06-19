package com.markbolo.event;

import com.markbolo.event.store.EventStore;
import com.markbolo.event.store.StoredEvent;

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
    private final EventStore eventStore;

    public EventPublisher(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public static void createInstance(EventStore eventStore) {
        instance = new EventPublisher(eventStore);
    }

    public static EventPublisher instance() {
        return instance;
    }

    public void produce(final String topic, final String tag, final Object message) {
        StoredEvent storedEvent = new StoredEvent(topic, tag, message);
        eventStore.store(storedEvent);
    }

    public void produce(final Event event) {
        StoredEvent storedEvent = new StoredEvent(event.topic(), event.tag(), event);
        eventStore.store(storedEvent);
    }
}
