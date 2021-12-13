package com.markbolo.event.store;

import java.util.List;

public class MysqlEventStore implements EventStore {

    private final EventRepository eventRepository;

    public MysqlEventStore(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void store(StoreEvent event) {
//        eventRepository.save(event.eventBean());
    }

    @Override
    public List<StoreEvent> waiting() {
        return null;
    }
}
