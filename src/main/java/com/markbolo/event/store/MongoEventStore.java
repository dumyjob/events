package com.markbolo.event.store;

import java.util.List;

public class MongoEventStore implements EventStore {

    @Override
    public void store(StoredEvent event) {

    }

    @Override
    public void updated(StoredEvent event) {

    }

    @Override
    public void release() {

    }

    @Override
    public List<StoredEvent> getWaiting() {
        return null;
    }
}
