package com.markbolo.event.store;

import java.util.List;

public interface EventStore {

    void store(StoredEvent event);

    void updated(StoredEvent event);

    void release();

    List<StoredEvent> getWaiting();
}
