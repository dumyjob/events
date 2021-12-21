package com.markbolo.event.scheduler;

import com.markbolo.event.publisher.EventPublisher;
import com.markbolo.event.store.EventStore;
import com.markbolo.event.store.StoreEvent;

import java.util.List;


public class EventScheduler {

    private final EventPublisher eventPublisher;

    private final EventStore eventStore;

    public EventScheduler(EventPublisher eventPublisher, EventStore eventStore) {
        this.eventPublisher = eventPublisher;
        this.eventStore = eventStore;
    }

    public void schedule() {
        try {
            // 分布式问题处理
            List<StoreEvent> events = eventStore.getWaiting();
            for (StoreEvent event : events) {
                event.processing();
                eventStore.updated(event);

                event.completed();
                eventStore.updated(event);

                eventPublisher.publish(event.topic(), event.tag(), event.message());
            }
        } finally {
            eventStore.release();
        }
    }
}
