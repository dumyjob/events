package com.markbolo.event.storage;

import com.markbolo.event.producer.EventProducer;
import com.markbolo.event.storage.dao.EventBean;
import com.markbolo.event.storage.dao.EventMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DataBaseEventStorage implements EventStorage {

    // 需要屏蔽mybatis/hibernate/jpa/jdbc...等的差异
    private final EventMapper eventMapper;
    private final EventProducer eventProducer;

    public DataBaseEventStorage(EventMapper eventMapper,
                                EventProducer eventProducer) {
        this.eventProducer = eventProducer;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional
    public void store(Event event) {
        /*
         * 没有使用DDD架构来构建,没有必要
         */
        EventBean eventBean = event.eventBean();
        eventMapper.insertSelective(eventBean);

        /*
         * 事务的可靠性方案和事务的存储介质绑定在一起了
         * 可能后续的事务可靠性的实现就不是基于同一个事务来实现,可能是XA事务,也可能是其他解决方案
         */
        StoredEvent storedEvent = new StoredEvent(eventBean);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                eventProducer.publish(storedEvent.topic(), storedEvent.tag(), storedEvent.message());

                // 依赖于tk-mybatis插入之后返回的ID进行更新
                storedEvent.completed();
                updated(storedEvent);
            }
        });
    }

    @Override
    public void updated(StoredEvent event) {
        EventBean eventBean = event.eventBean();
        eventMapper.updateByPrimaryKeySelective(eventBean);
    }

    @Override
    public void release() {
        eventMapper.release(EventStatus.PUBLISHING, EventStatus.UN_PUBLISHED);
    }


    @Override
    public List<StoredEvent> unpublishedEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<EventBean> eventBeans = eventMapper.getUnPublishEvents(now, EventStatus.UN_PUBLISHED);

        return eventBeans.stream()
                .map(StoredEvent::new)
                .collect(Collectors.toList());
    }
}
