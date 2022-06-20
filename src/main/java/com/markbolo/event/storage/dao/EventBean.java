package com.markbolo.event.storage.dao;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class EventBean {

    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    private String tag;

    private String message;

    private String status;

    private String trackerId;

    private Long delay;
}
