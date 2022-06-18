package com.markbolo.event.scheduler;

import com.markbolo.event.producer.EventProducer;
import com.markbolo.event.store.EventStore;
import com.markbolo.event.store.StoredEvent;

import java.util.List;


public class EventScheduler {

    private final EventProducer eventProducer;

    private final EventStore eventStore;

    public EventScheduler(EventProducer eventProducer, EventStore eventStore) {
        this.eventProducer = eventProducer;
        this.eventStore = eventStore;
    }

    public void schedule() {
        try {
            // 分布式问题处理
            List<StoredEvent> events = eventStore.getWaiting();
            for (StoredEvent event : events) {
                event.processing();
                eventStore.updated(event);

                event.completed();
                eventStore.updated(event);

                eventProducer.publish(event.topic(), event.tag(), event.message());
            }
        } finally {
            eventStore.release();
        }
    }
}
