package com.markbolo.event.consumer.registry;

import com.markbolo.event.consumer.adpater.MessageConsumer;

import java.util.List;

public interface ConsumerFinder {

    List<MessageConsumer> findConsumers();
}
