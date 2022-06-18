package com.markbolo.event.consumer.adpater;

import com.markbolo.event.MessageConverter;
import com.markbolo.event.consumer.ConsumerProperty;

/**
 * 1. 多线程consumer消费
 * 2. 消息ack/no_ack
 * 3. 异常增强处理 (是否需要添加consumer监控)
 * 4.
 */
public abstract class AbstractMessageConsumer<T> implements MessageConsumer, Restartable {

    protected final ConsumerProperty consumerProperty;

    protected final MessageConverter messageConverter;

    protected final ConsumerHandler<T> handler;

    protected AbstractMessageConsumer(ConsumerProperty consumerProperty,
                                      MessageConverter messageConverter,
                                      ConsumerHandler<T> handler) {
        this.consumerProperty = consumerProperty;
        this.messageConverter = messageConverter;
        this.handler = handler;
    }


    @Override
    public String name() {
        return consumerProperty.getName();
    }

    /**
     * 实现各种Broker的消息订阅逻辑
     */
    public abstract void subscribe();
}
