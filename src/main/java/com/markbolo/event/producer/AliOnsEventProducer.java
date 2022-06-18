package com.markbolo.event.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.markbolo.event.MessageConverter;

import java.nio.charset.StandardCharsets;

public class AliOnsEventProducer implements EventProducer {

    private final Producer producer;

    private final MessageConverter messageConverter;

    public AliOnsEventProducer(Producer producer,
                               MessageConverter messageConverter) {
        this.producer = producer;
        this.messageConverter = messageConverter;
    }

    @Override
    public void publish(String topic, String tag, Object message) {
        try {
            /*
            * 底层也是使用的RocketMq, 也是支持多tag模式的
             */
            byte[] body = messageConverter.format(message).getBytes(StandardCharsets.UTF_8);
            Message msg = new Message(topic, tag, body);
            producer.send(msg);
        } catch (Exception e) {
            throw new MqProduceException("message produce exception:" + e.getMessage(), e);
        }
    }
}
