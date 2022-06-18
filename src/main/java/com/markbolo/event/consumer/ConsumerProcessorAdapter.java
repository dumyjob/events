package com.markbolo.event.consumer;

import org.springframework.core.ResolvableType;

/**
 * 提供构造方法方式的ConsumerProcessor实现
 */
public abstract class ConsumerProcessorAdapter<T> implements ConsumerProcessor<T> {

    private String name;

    private final Class<?> genericType;

    protected ConsumerProcessorAdapter(){
        ResolvableType resolvableType = ResolvableType.forClass(this.getClass());
        genericType = resolvableType.getSuperType().getGeneric(0).resolve();
    }

    public ConsumerProcessorAdapter(String name) {
        this();
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> getGenericType() {
        return genericType;
    }
}
