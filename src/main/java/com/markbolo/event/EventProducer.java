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
public class EventProducer {

    private final EventStore eventStore;

    public EventProducer(EventStore eventStore) {
        this.eventStore = eventStore;
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
