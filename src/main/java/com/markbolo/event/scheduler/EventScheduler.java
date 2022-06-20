package com.markbolo.event.scheduler;

import com.markbolo.event.producer.EventProducer;
import com.markbolo.event.storage.EventStorage;
import com.markbolo.event.storage.StoredEvent;

import java.util.List;


public class EventScheduler {

    private final EventProducer eventProducer;

    private final EventStorage eventStorage;

    public EventScheduler(EventProducer eventProducer,
                          EventStorage eventStorage) {
        this.eventProducer = eventProducer;
        this.eventStorage = eventStorage;
    }

    public void schedule() {
        try {
            // 分布式问题处理
            List<StoredEvent> events = eventStorage.unpublishedEvents();
            for (StoredEvent event : events) {
                event.processing();
                eventStorage.updated(event);

                event.completed();
                eventStorage.updated(event);

                eventProducer.publish(event.topic(), event.tag(), event.message());
            }
        } finally {
            eventStorage.release();
        }
    }
}
