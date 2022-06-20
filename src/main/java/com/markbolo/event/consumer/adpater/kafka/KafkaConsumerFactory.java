package com.markbolo.event.consumer.adpater.kafka;

import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import com.markbolo.event.converter.MessageConverter;


public class KafkaConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;


    public KafkaConsumerFactory(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperty consumerProperty, ConsumerHandler<T> consumer) {
        return new KafkaMessageConsumer<>(consumerProperty, messageConverter, consumer);
    }
}
