package com.markbolo.event.consumer.adpater.rabbit;

import com.markbolo.event.MessageConverter;
import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.MessageConsumerFactory;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.consumer.adpater.MessageConsumer;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumerFactory implements MessageConsumerFactory {

    private final MessageConverter messageConverter;

    // TODO 需要处理rabbitmq connectionFactory
    private final ConnectionFactory connectionFactory;

    @Autowired
    public RabbitConsumerFactory(MessageConverter messageConverter,
                                 ConnectionFactory connectionFactory) {
        this.messageConverter = messageConverter;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public <T> MessageConsumer createConsumer(ConsumerProperty consumerProperty, ConsumerHandler<T> consumer) {
        return new RabbitMessageConsumer<>(consumerProperty, messageConverter, consumer, connectionFactory);
    }
}
