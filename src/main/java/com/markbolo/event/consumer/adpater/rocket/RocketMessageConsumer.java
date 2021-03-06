package com.markbolo.event.consumer.adpater.rocket;

import com.markbolo.event.consumer.ConsumerException;
import com.markbolo.event.consumer.ConsumerProperties;
import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.adpater.AbstractMessageConsumer;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.converter.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

@Slf4j
public class RocketMessageConsumer<T> extends AbstractMessageConsumer<T> {

    private DefaultMQPushConsumer consumer;
    private final RocketProperties rocketProperties;

    public RocketMessageConsumer(ConsumerProperty consumerProperty,
                                 MessageConverter messageConverter,
                                 ConsumerHandler<T> handler,
                                 RocketProperties rocketProperties) {
        super(consumerProperty, messageConverter, handler);
        this.rocketProperties = rocketProperties;
    }

    @Override
    public void start() {
        if (null == this.consumerProperty) {
            throw new ConsumerException("properties not set");
        }

        if (this.consumer != null && this.started.get()) {
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
        if (this.consumer == null || !this.started.get()) {
            if (log.isInfoEnabled()) {
                log.info("consumer:{} already shutdown.", consumerProperty.getName());
            }
            return;
        }

        if (this.started.compareAndSet(true, false)) {
            this.consumer.shutdown();
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
        try {
            ConsumerProperties.ConsumerConfiguration configuration = consumerProperty.getConfiguration();
            this.consumer = new DefaultMQPushConsumer(configuration.getConsumerGroup());
            // nameserver
            consumer.setNamesrvAddr(rocketProperties.getNameServer());
            consumer.setInstanceName(rocketProperties.getInstanceName());
            // consumer配置
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setConsumeThreadMin(configuration.getThreadNum());     // 批量并发线程消费
            consumer.subscribe(configuration.getTopic(), configuration.getTag());
            consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
                try {
                    for (MessageExt message : messages) {
                        T body = messageConverter.from(message.getBody(), handler.getGenericType());
                        handler.handle(body);
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    // 这里消息增强处理,日志监控 & 消息告警等 (能否通过切面等非侵入的方式完成)
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            throw new ConsumerException(e);
        }
    }
}
