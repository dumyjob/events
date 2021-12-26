package com.markbolo.event.publisher;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.markbolo.event.MessageConverter;
import com.markbolo.event.MqProduceException;

import java.nio.charset.StandardCharsets;

public class AliOnsEventPublisher implements EventPublisher {

    private final Producer producer;

    private final MessageConverter messageConverter;

    public AliOnsEventPublisher(Producer producer,
                                MessageConverter messageConverter) {
        this.producer = producer;
        this.messageConverter = messageConverter;
    }

    @Override
    public void publish(String topic, String tag, Object message) {
        try {
            Message msg = new Message(topic, tag, messageConverter.format(message).getBytes(StandardCharsets.UTF_8));
            producer.send(msg);
        } catch (Exception e) {
            throw new MqProduceException("message produce exception:" + e.getMessage(), e);
        }
    }
}
