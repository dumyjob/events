package com.markbolo.event.consumer.adpater.alions;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.markbolo.event.consumer.ConsumerProperties;
import com.markbolo.event.consumer.ConsumerProperty;
import com.markbolo.event.consumer.adpater.AbstractMessageConsumer;
import com.markbolo.event.consumer.adpater.ConsumerHandler;
import com.markbolo.event.converter.MessageConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class AliOnsMessageConsumer<T> extends AbstractMessageConsumer<T> {

    private Consumer consumer;
    protected final AtomicBoolean started = new AtomicBoolean(false);

    public AliOnsMessageConsumer(ConsumerProperty consumerProperty,
                                 MessageConverter messageConverter,
                                 ConsumerHandler<T> handler) {
        super(consumerProperty, messageConverter, handler);
    }

    @Override
    public void start() {
        if (null == this.consumerProperty) {
            throw new ONSClientException("properties not set");
        }

        if (this.consumer != null && this.consumer.isStarted()) {
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
        if (this.consumer == null || this.consumer.isClosed()) {
            if (log.isInfoEnabled()) {
                log.info("consumer:{} already shutdown.", consumerProperty.getName());
            }
            return;
        }

        // 控制并发
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
        // TODO ali ons connection在哪里处理的?? NameServer and soon
        ConsumerProperties.ConsumerConfiguration configuration = consumerProperty.getConfiguration();
        String tag = configuration.getTag();
        Integer threadNum = configuration.getThreadNum();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.GROUP_ID, configuration.getConsumerGroup());
        properties.put(PropertyKeyConst.ConsumeThreadNums, threadNum);

        this.consumer = ONSFactory.createConsumer(properties);
        consumer.start();
        consumer.subscribe(configuration.getTopic(), tag, (message, context) -> {
            try {
                T body = messageConverter.from(message.getBody(), handler.getGenericType());
                handler.handle(body);
                return Action.CommitMessage;
            } catch (Exception e) {
                return Action.ReconsumeLater;
            }
        });
    }
}
