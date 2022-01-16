package com.markbolo.event.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.markbolo.event.MessageConsumer;
import com.markbolo.event.MessageConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;

public class AliOnsMessageConsumer extends AbstractMessageConsumer {

    public AliOnsMessageConsumer(ConsumerProperties.ConsumerConfiguration configuration,
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
        return configuration.getConsumerGroup();
    }

    @Override
    public <T> MessageConsumer subscribe(java.util.function.Consumer<T> handler) {
        // ali ons connection在哪里处理的??
        String tag = configuration.getTag();
        Integer threadNum = configuration.getThreadNum();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ConsumerId, configuration.getConsumerGroup());
        properties.put(PropertyKeyConst.ConsumeThreadNums, threadNum);

        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.start();
        consumer.subscribe(configuration.getTopic(), tag, (message, context) -> {
            try {
                Type type = handler.getClass().getGenericSuperclass();
                Type messageType = ((ParameterizedType) type).getActualTypeArguments()[0];
                Class<T> clazz = (Class<T>) messageType;

                T body = messageConverter.from(message.getBody(), clazz);
                handler.accept(body);
                return Action.CommitMessage;
            } catch (Exception e) {
                return Action.ReconsumeLater;
            } finally {

            }
        });

        return this;
    }
}
