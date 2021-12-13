package com.markbolo.event.scheduler;

import com.markbolo.event.publisher.EventPublisher;

// 是否使用xxl-job??
public class EventScheduler {

    private final EventPublisher eventPublisher;

    public EventScheduler(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void schedule(){


    }
}
