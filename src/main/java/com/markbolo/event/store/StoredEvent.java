package com.markbolo.event.store;

import com.markbolo.event.Event;

/**
 * 存储事件
 */
public class StoredEvent {

    private final String topic;

    private final String tag;

    private final Object message;

    private EventStatus status;

    private String trackerId;

    public StoredEvent(String topic, String tag, Object message) {
        this.topic = topic;
        this.tag = tag;
        this.message = message;
        this.status = EventStatus.WAIT;
        if (message.getClass().isAssignableFrom(Event.class)) {
            this.trackerId = ((Event) message).trackerId();
        }
    }

    public StoredEvent(EventBean eventBean){
        this.topic = eventBean.getTopic();
        this.tag = eventBean.getTag();
        this.message = eventBean.getMessage();
        this.status = EventStatus.valueOf(eventBean.getStatus());
        this.trackerId = eventBean.getTrackerId();
    }


    public EventBean eventBean() {
        EventBean eventBean = new EventBean();
        eventBean.setTopic(this.topic);
        eventBean.setTag(this.tag);
        eventBean.setStatus(status.toString());
        // TODO 转换String jackson/ gson / xml?? MessageConverter
        eventBean.setMessage(this.message.toString());
        eventBean.setTrackerId(this.trackerId);
        return eventBean;
    }

    public void completed() {
        this.status = EventStatus.SUCCESS;
    }

    public void processing(){
        this.status = EventStatus.PROCESSING;
    }

    public String topic(){
        return topic;
    }

    public String tag(){
        return tag;
    }

    public Object message(){
        return message;
    }
}
