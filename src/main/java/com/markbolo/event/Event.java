package com.markbolo.event;

public interface Event {

    String topic();

    String tag();

    default String trackerId(){
        return "";
    }
}
