package com.markbolo.event;

import com.markbolo.event.publisher.EventPublisher;
import com.markbolo.event.store.EventStore;
import com.markbolo.event.store.StoreEvent;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class EventProducer {

    private final EventStore eventStore;

    private final EventPublisher eventPublisher;

    public EventProducer(EventStore eventStore,
                         EventPublisher eventPublisher) {
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
    }

    public void produce(final String topic, final String tag, final Object message) {
        StoreEvent storeEvent = new StoreEvent(topic, tag, message);
        eventStore.store(storeEvent);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 缺少EventId去更新
                storeEvent.completed();
                eventStore.updated(storeEvent);

                eventPublisher.publish(topic, tag, message);
            }
        });
    }


    public void produce(final Event event) {
        StoreEvent storeEvent = new StoreEvent(event.topic(), event.tag(), event);
        eventStore.store(storeEvent);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                storeEvent.completed();
                eventStore.updated(storeEvent);

                eventPublisher.publish(event.topic(), event.tag(), event);
            }
        });
    }
}
