package com.markbolo.event.store;

import java.util.List;

public class MongoEventStore implements EventStore{

    @Override
    public void store(StoreEvent event) {

    }

    @Override
    public List<StoreEvent> waiting() {
        return null;
    }
}
