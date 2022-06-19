package com.markbolo.event.storage;

import java.util.List;

public interface EventStorage {

    void store(Event event);

    void updated(StoredEvent event);

    void release();

    List<StoredEvent> unpublishedEvents();
}
