package com.markbolo.event.storage;

import java.util.List;

public class MongoEventStorage implements EventStorage {

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
    public List<StoredEvent> unpublishedEvents() {
        return null;
    }
}
