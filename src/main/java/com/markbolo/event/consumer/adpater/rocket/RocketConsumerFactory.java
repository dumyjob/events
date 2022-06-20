package com.markbolo.event.consumer.adpater.rocket;

import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import com.markbolo.event.converter.MessageConverter;

public class RocketConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;

    public RocketConsumerFactory(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }


    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperty consumerProperty, ConsumerHandler<T> consumer) {
        return new RocketMessageConsumer<>(consumerProperty, messageConverter, consumer);
    }
}
