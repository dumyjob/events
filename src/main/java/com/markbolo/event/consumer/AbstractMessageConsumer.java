package com.markbolo.event.consumer;

import com.markbolo.event.MessageConsumer;
import com.markbolo.event.MessageConverter;

import java.util.function.Consumer;

public abstract class AbstractMessageConsumer implements MessageConsumer {

    protected final ConsumerProperties.ConsumerConfiguration configuration;

    protected final MessageConverter messageConverter;


    protected  AbstractMessageConsumer(ConsumerProperties.ConsumerConfiguration configuration,
                                       MessageConverter messageConverter) {
        this.configuration = configuration;
        this.messageConverter = messageConverter;
    }

    // TODO 这里不太适合 subscribe
    public abstract  <T> MessageConsumer subscribe(Consumer<T> handler);
}
