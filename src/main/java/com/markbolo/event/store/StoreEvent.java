package com.markbolo.event.store;

import com.markbolo.event.Event;

public class StoreEvent {

    private final String topic;

    private final String tag;

    private final Object message;

    private EventStatus status;

    private String trackerId;

    public StoreEvent(String topic, String tag, Object message) {
        this.topic = topic;
        this.tag = tag;
        this.message = message;
        this.status = EventStatus.WAIT;
        if (message.getClass().isAssignableFrom(Event.class)) {
            this.trackerId = ((Event) message).trackerId();
        }
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
}
