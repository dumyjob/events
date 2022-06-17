package com.markbolo.event;

import com.markbolo.event.publisher.EventPublisher;
import com.markbolo.event.store.EventStore;
import com.markbolo.event.store.StoredEvent;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * USAGE:
 *
 * EventProducer.createInstance()
 *
 * EventProducer.instance()
 *              .publish(final T domainEvent);
 */
public class EventProducer {

    private final EventStore eventStore;

    private final EventPublisher eventPublisher;

    public EventProducer(EventStore eventStore,
                         EventPublisher eventPublisher) {
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
    }

    public void produce(final String topic, final String tag, final Object message) {
        StoredEvent storedEvent = new StoredEvent(topic, tag, message);
        eventStore.store(storedEvent);
    }


    public void produce(final Event event) {
        StoredEvent storedEvent = new StoredEvent(event.topic(), event.tag(), event);
        eventStore.store(storedEvent);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                storedEvent.completed();
                eventStore.updated(storedEvent);

                eventPublisher.publish(event.topic(), event.tag(), event);
            }
        });
    }
}
