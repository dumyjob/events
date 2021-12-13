package com.markbolo.event.store;

import java.util.List;

public interface EventStore {

    void store(StoreEvent event);

    List<StoreEvent > waiting();
}
