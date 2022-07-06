package com.markbolo.event.consumer.adpater.rabbit;

import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.markbolo.event.consumer.ConsumerException;
import com.markbolo.event.consumer.ConsumerProperties;
import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.adpater.AbstractMessageConsumer;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.converter.MessageConverter;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitMessageConsumer<T> extends AbstractMessageConsumer<T> {

    private final ConnectionFactory connectionFactory;

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
        try (Connection connection = connectionFactory.newConnection()) { // 这里connection被关闭了是否有问题
            this.channel = connection.createChannel();
            ConsumerProperties.ConsumerConfiguration configuration = consumerProperty.getConfiguration();
            boolean autoAck = false;
            // TODO rabbitMq 多线程消费, 参考RabbitListener
            channel.basicConsume(configuration.getTopic(), autoAck, configuration.getTag(), new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    try {
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
