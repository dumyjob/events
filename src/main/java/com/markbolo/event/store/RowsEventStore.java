package com.markbolo.event.store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RowsEventStore implements EventStore {

    // 需要屏蔽mybatis/hibernate/jpa/jdbc...等的差异
    private final EventRepository eventRepository;

    public RowsEventStore(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void store(StoreEvent event) {
        EventBean eventBean = event.eventBean();
        eventRepository.insertSelective(eventBean);
    }

    @Override
    public void updated(StoreEvent event) {
        EventBean eventBean = event.eventBean();
        eventRepository.updateByPrimaryKeySelective(eventBean);
    }

    @Override
    public void release() {
        eventRepository.release(EventStatus.PROCESSING, EventStatus.WAIT);
    }


    @Override
    public List<StoreEvent> getWaiting() {
        LocalDateTime now = LocalDateTime.now();
        List<EventBean> eventBeans = eventRepository.selectWaiting(now, EventStatus.WAIT);

        return eventBeans.stream()
                .map(StoreEvent::new)
                .collect(Collectors.toList());
    }
}
