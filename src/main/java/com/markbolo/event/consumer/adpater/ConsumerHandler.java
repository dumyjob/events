package com.markbolo.event.consumer.adpater;

import org.springframework.core.ResolvableType;

import java.util.function.Consumer;

public class ConsumerHandler<T> {

    private final Consumer<T> consumer;

    private final Class<T> genericType;

    public ConsumerHandler(Consumer<T> consumer) {
        this.consumer = consumer;
        ResolvableType resolvableType = ResolvableType.forClass(consumer.getClass());
        this.genericType = (Class<T>) resolvableType.getSuperType()
                .getGeneric(0)
                .resolve();
    }

    public void handle(T message) {
        consumer.accept(message);
    }

    public Class<T> getGenericType() {
        return genericType;
    }
}
