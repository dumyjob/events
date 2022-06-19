package com.markbolo.event.storage;

import com.markbolo.event.storage.dao.EventBean;

public class Event {

    private String topic;
    private String tag;
    private String type;
    private Object body;

    public Event(String topic, String tag, Object body) {
        this.topic = topic;
        this.tag = tag;
        this.body = body;
    }

    public Event(com.markbolo.event.Event event) {
        this.tag = event.tag();
        this.topic = event.topic();
        this.body = event;
    }


    public EventBean eventBean() {
        EventBean eventBean = new EventBean();
        eventBean.setTopic(this.topic);
        eventBean.setTag(this.tag);
        eventBean.setStatus(EventStatus.PUBLISHING.name());
        // TODO 转换String jackson/ gson / xml?? MessageConverter
        eventBean.setMessage(this.body.toString());
        return eventBean;
    }

}
