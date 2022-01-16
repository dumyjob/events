package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;
import com.markbolo.event.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class AliOnsConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;

    @Autowired
    public AliOnsConsumerFactory(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperties.ConsumerConfiguration configuration, Consumer<T> consumer) {
        return new AliOnsMessageConsumer(configuration, messageConverter)
                .subscribe(consumer);
    }
}
