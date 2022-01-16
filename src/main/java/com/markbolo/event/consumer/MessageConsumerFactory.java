package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;

import java.util.function.Consumer;

public interface MessageConsumerFactory {

    <T> MessageConsumer createConsumer(ConsumerProperties.ConsumerConfiguration configuration, Consumer<T> consumer);
}
