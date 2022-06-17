package com.markbolo.event;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * 序列化使用场景:
 * 1. 将DomainEvent转换为StoredEvent.body
 * 2. 将StoredEvent投递到Message Broker的时候的消息内容的序列化
 * 3. Message Consumer在消费的时候将Broker投递的Message转换为指定Message-Object
 */
public interface MessageConverter {

    String format(Object message) throws JsonProcessingException;

    <T> T from(String message, Class<T> clazz) throws IOException;

    <T> T from(byte[] message, Class<T> clazz) throws IOException;
}
