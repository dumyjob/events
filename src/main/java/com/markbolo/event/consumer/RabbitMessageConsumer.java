package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;
import com.markbolo.event.MessageConverter;
import com.markbolo.event.MqConsumeException;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitMessageConsumer extends AbstractMessageConsumer {

    private final ConnectionFactory connectionFactory;

    public RabbitMessageConsumer(ConsumerProperties.ConsumerConfiguration configuration,
                                 MessageConverter messageConverter,
                                 ConnectionFactory connectionFactory) {
        super(configuration, messageConverter);
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean started() {
        return false;
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public String consumerId() {
        return null;
    }

    @Override
    public <T> MessageConsumer subscribe(Consumer<T> handler) throws MqConsumeException {
        // 需要处理rabbitmq connection
        try (Connection connection = connectionFactory.newConnection()) {
            Channel channel = connection.createChannel();

            // TODO 理解一下rabbitmq exchange routingKey 看是否必须
            channel.exchangeDeclare(configuration.getExchange(), "topic");
            channel.queueDeclare(configuration.getTopic(), true, false, false, null);
            channel.queueBind(configuration.getTopic(), configuration.getExchange(), configuration.getRoutingKey());

            channel.basicConsume(configuration.getTopic(), false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    try {
                        // 如果不是需要的tag如何处理??
                        Type type = handler.getClass().getGenericSuperclass();
                        Type messageType = ((ParameterizedType) type).getActualTypeArguments()[0];
                        Class<T> clazz = (Class<T>) messageType;

                        T message = messageConverter.from(body, clazz);
                        handler.accept(message);
                    } catch (IOException e) {
                        // TODO 需要处理 ack
                    }
                }
            });
        } catch (IOException | TimeoutException e) {
            throw new MqConsumeException(e);
        }

        return this;
    }
}
