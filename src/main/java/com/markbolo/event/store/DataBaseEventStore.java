package com.markbolo.event.store;

import com.markbolo.event.publisher.EventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DataBaseEventStore implements EventStore {

    // 需要屏蔽mybatis/hibernate/jpa/jdbc...等的差异
    private final EventRepository eventRepository;
    private final EventPublisher eventPublisher;

    public DataBaseEventStore(EventRepository eventRepository,
                              EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public void store(StoredEvent event) {
        EventBean eventBean = event.eventBean();
        eventRepository.insertSelective(eventBean);

        /*
         * 事务的可靠性方案和事务的存储介质绑定在一起了
         * 可能后续的事务可靠性的实现就不是基于同一个事务来实现,可能是XA事务,也可能是其他解决方案
         */
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 缺少EventId去更新
                event.completed();
                updated(event);

                eventPublisher.publish(event.topic(), event.tag(), event.message());
            }
        });
    }

    @Override
    public void updated(StoredEvent event) {
        EventBean eventBean = event.eventBean();
        eventRepository.updateByPrimaryKeySelective(eventBean);
    }

    @Override
    public void release() {
        eventRepository.release(EventStatus.PROCESSING, EventStatus.WAIT);
    }


    @Override
    public List<StoredEvent> getWaiting() {
        LocalDateTime now = LocalDateTime.now();
        List<EventBean> eventBeans = eventRepository.selectWaiting(now, EventStatus.WAIT);

        return eventBeans.stream()
                .map(StoredEvent::new)
                .collect(Collectors.toList());
    }
}
