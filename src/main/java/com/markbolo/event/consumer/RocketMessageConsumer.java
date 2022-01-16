package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;
import com.markbolo.event.MessageConverter;
import com.markbolo.event.MqConsumeException;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

public class RocketMessageConsumer extends AbstractMessageConsumer {

    public RocketMessageConsumer(ConsumerProperties.ConsumerConfiguration configuration,
                                 MessageConverter messageConverter) {
        super(configuration, messageConverter);
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
    public <T> MessageConsumer subscribe(Consumer<T> handler) {
        // rocketmq connection在哪里处理??
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerId());
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.subscribe(configuration.getTopic(), configuration.getTag());
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                    // 批量并发线程消费??
                    try {
                        Type type = handler.getClass().getGenericSuperclass();
                        Type messageType = ((ParameterizedType) type).getActualTypeArguments()[0];
                        Class<T> clazz = (Class<T>) messageType;
                        for (MessageExt message : messages) {
                            T body = messageConverter.from(message.getBody(), clazz);
                            handler.accept(body);
                        }
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } catch (Exception e) {
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
            });
        } catch (MQClientException e) {
            throw new MqConsumeException(e);
        }

        return this;
    }
}
