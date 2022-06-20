package com.markbolo.event.storage;

import com.markbolo.event.storage.dao.EventBean;

public class Event {

    private String topic;
    private String tag;
    private String type;
    private String body;

    public Event(String topic, String tag, String body) {
        this.topic = topic;
        this.tag = tag;
        this.body = body;
    }

    public EventBean eventBean() {
        EventBean eventBean = new EventBean();
        eventBean.setTopic(this.topic);
        eventBean.setTag(this.tag);
        eventBean.setStatus(EventStatus.PUBLISHING.name());
        eventBean.setMessage(this.body);
        return eventBean;
    }
}
