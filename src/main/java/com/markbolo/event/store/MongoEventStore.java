package com.markbolo.event.store;

import java.util.List;

public class MongoEventStore implements EventStore {

    @Override
    public void store(StoreEvent event) {

    }

    @Override
    public void updated(StoreEvent event) {

    }

    @Override
    public void release() {

    }

    @Override
    public List<StoreEvent> getWaiting() {
        return null;
    }
}
