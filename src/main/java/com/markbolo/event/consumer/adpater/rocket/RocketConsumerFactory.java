package com.markbolo.event.consumer.adpater.rocket;

import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import com.markbolo.event.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RocketConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;

    @Autowired
    public RocketConsumerFactory(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }


    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperty consumerProperty, ConsumerHandler<T> consumer) {
        return new RocketMessageConsumer<>(consumerProperty, messageConverter, consumer);
    }
}
