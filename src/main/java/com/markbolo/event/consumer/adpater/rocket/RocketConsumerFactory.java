package com.markbolo.event.consumer.adpater.rocket;

import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import com.markbolo.event.converter.MessageConverter;

public class RocketConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;
    private final RocketProperties rocketProperties;

    public RocketConsumerFactory(MessageConverter messageConverter,
                                 RocketProperties rocketProperties) {
        this.messageConverter = messageConverter;
        this.rocketProperties = rocketProperties;
    }


    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperty consumerProperty, ConsumerHandler<T> consumer) {
        return new RocketMessageConsumer<>(consumerProperty, messageConverter, consumer, rocketProperties);
    }
}
