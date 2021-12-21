package com.markbolo.event.store;

import java.util.List;

public interface EventStore {

    void store(StoreEvent event);

    void updated(StoreEvent event);

    void release();

    List<StoreEvent> getWaiting();
}
