package com.markbolo.event.consumer.adpater.alions;

import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import com.markbolo.event.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliOnsConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;

    @Autowired
    public AliOnsConsumerFactory(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperty consumerProperty, ConsumerHandler<T> consumer) {
        return new AliOnsMessageConsumer<>(consumerProperty, messageConverter, consumer);
    }
}
