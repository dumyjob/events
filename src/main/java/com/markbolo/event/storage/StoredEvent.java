package com.markbolo.event.storage;

import com.markbolo.event.storage.dao.EventBean;

/**
 * 存储事件
 * 整理Event & StoredEvent & EventBean之间的关系
 */
public class StoredEvent {

    private final String body;

    private final String topic;

    private final String tag;

    private Long eventId;
    private EventStatus status;
    private String trackerId;

    public StoredEvent(EventBean eventBean) {
        this.eventId = eventBean.getId();
        this.topic = eventBean.getTopic();
        this.tag = eventBean.getTag();
        this.body = eventBean.getMessage();
        this.status = EventStatus.valueOf(eventBean.getStatus());
        this.trackerId = eventBean.getTrackerId();
    }

    public EventBean eventBean() {
        EventBean eventBean = new EventBean();

        // 仅会更新status
        eventBean.setId(this.eventId);
        eventBean.setStatus(this.status.name());

        return eventBean;
    }

    public void completed() {
        this.status = EventStatus.PUBLISHED;
    }

    public void processing() {
        this.status = EventStatus.PUBLISHING;
    }

    public String topic() {
        return topic;
    }

    public String tag() {
        return tag;
    }

    public String message() {
        return body;
    }
}
