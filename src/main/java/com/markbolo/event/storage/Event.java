package com.markbolo.event.storage;

import com.markbolo.event.storage.dao.EventBean;
import lombok.Getter;

@Getter
public class Event {

    private final String topic;
    private final String tag;
    private final String body;
    private final long delay;

    public Event(String topic, String tag, String body, long delay) {
        this.topic = topic;
        this.tag = tag;
        this.body = body;
        this.delay = delay;
    }

    public EventBean eventBean() {
        EventBean eventBean = new EventBean();
        eventBean.setTopic(this.topic);
        eventBean.setTag(this.tag);
        eventBean.setStatus(EventStatus.PUBLISHING.name());
        eventBean.setMessage(this.body);
        eventBean.setDelay(delay);
        return eventBean;
    }
}
