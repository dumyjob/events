package com.markbolo.event.store;

import lombok.Data;

@Data
public class EventBean {

    private Long id;

    private String topic;

    private String tag;

    private String message;

    private String status;

    private String trackerId;
}
