package com.markbolo.event.consumer.adpater.rabbit;

import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.markbolo.event.consumer.ConsumerException;
import com.markbolo.event.consumer.ConsumerProperties;
import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.adpater.AbstractMessageConsumer;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.converter.MessageConverter;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class RabbitMessageConsumer<T> extends AbstractMessageConsumer<T> {

    private final ConnectionFactory connectionFactory;

    private final AtomicBoolean started = new AtomicBoolean(false);

    private Channel channel;

    protected RabbitMessageConsumer(ConsumerProperty consumerProperty,
                                    MessageConverter messageConverter,
                                    ConsumerHandler<T> handler,
                                    ConnectionFactory connectionFactory) {
        super(consumerProperty, messageConverter, handler);
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void start() {
        if (null == this.consumerProperty) {
            throw new ONSClientException("properties not set");
        }

        if (this.channel != null && this.started.get()) {
            if (log.isInfoEnabled()) {
                log.info("consumer:{} already started.", consumerProperty.getName());
            }
            return;
        }

        if (this.started.compareAndSet(false, true)) {
            // 避免并发影响
            subscribe();
        }
    }

    @Override
    public void close() {
        if (this.channel == null || !this.started.get()) {
            if (log.isInfoEnabled()) {
                log.info("consumer:{} already shutdown.", consumerProperty.getName());
            }
            return;
        }

        if (this.started.compareAndSet(true, false)) {
            try {
                this.channel.close();
            } catch (Exception e) {
                throw new ConsumerException(String.format("consumer close fail, %s:%s ",
                        this.getClass(), this.consumerProperty.getName()), e);
            }
        }
    }

    @Override
    public boolean started() {
        return this.started.get();
    }

    @Override
    public boolean closed() {
        return !this.started.get();
    }


    @Override
    public void subscribe() {
        // 需要处理rabbitmq connection
        try (Connection connection = connectionFactory.newConnection()) {
            this.channel = connection.createChannel();
            // TODO 理解一下rabbitmq exchange routingKey 看是否必须
            ConsumerProperties.ConsumerConfiguration configuration = consumerProperty.getConfiguration();
            String patternTopic = "topic";
            channel.exchangeDeclare(configuration.getExchange(), patternTopic);
            channel.queueDeclare(configuration.getTopic(), true, false, false, null);
            channel.queueBind(configuration.getTopic(), configuration.getExchange(), configuration.getRoutingKey());

            boolean autoAck = false;
            channel.basicConsume(configuration.getTopic(), autoAck, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    try {
                        // 如果不是需要的tag如何处理??
                        T message = messageConverter.from(body, handler.getGenericType());
                        handler.handle(message);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (IOException e) {
                        //  是否要做增强处理, 例如对Consumer消费的监控
                        try {
                            channel.basicNack(envelope.getDeliveryTag(), false, true);
                        } catch (IOException ex) {
                            log.error("consumer n_ack fail, {}:{}", this.getClass(), consumerProperty.getName());
                        }
                    }
                }
            });
        } catch (IOException | TimeoutException e) {
            log.error("rabbit broker connection fail,", e);
            throw new ConsumerException(e);
        }
    }
}
