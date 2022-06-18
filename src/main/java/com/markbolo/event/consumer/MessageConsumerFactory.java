package com.markbolo.event.consumer;

import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;

public interface MessageConsumerFactory {

    <T> MessageConsumer createConsumer(ConsumerProperty consumerProperty, ConsumerHandler<T> consumer);
}
