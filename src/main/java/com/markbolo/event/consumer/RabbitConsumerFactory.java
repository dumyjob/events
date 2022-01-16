package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;
import com.markbolo.event.MessageConverter;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

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
    public <T> MessageConsumer createConsumer(ConsumerProperties.ConsumerConfiguration configuration, Consumer<T> consumer) {
        return new RabbitMessageConsumer(configuration, messageConverter, connectionFactory)
                .subscribe(consumer);
    }
}
