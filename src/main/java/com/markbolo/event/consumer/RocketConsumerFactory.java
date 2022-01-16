package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;
import com.markbolo.event.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class RocketConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;

    @Autowired
    public RocketConsumerFactory(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }


    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperties.ConsumerConfiguration configuration, Consumer<T> consumer) {
        return new RocketMessageConsumer(configuration, messageConverter)
                .subscribe(consumer);
    }
}
