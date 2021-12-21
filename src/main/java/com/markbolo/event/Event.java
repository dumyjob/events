package com.markbolo.event;

public interface Event {

    String topic();

    String tag();

    default long delay(){
        return 0L;
    }

    default String trackerId(){
        return "";
    }

    default String eventType(){
        return this.getClass().getName();
    }
}
