package com.markbolo.event.consumer;

public class ConsumerProperty {

    private final String name;

    private final ConsumerProperties.ConsumerConfiguration configuration;

    public ConsumerProperty(String name,
                            ConsumerProperties.ConsumerConfiguration config) {
        this.name = name;
        this.configuration = config;
    }

    public String getName() {
        return name;
    }

    public ConsumerProperties.ConsumerConfiguration getConfiguration() {
        return configuration;
    }
}
