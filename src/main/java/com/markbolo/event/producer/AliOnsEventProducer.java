package com.markbolo.event.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;

import java.nio.charset.StandardCharsets;

public class AliOnsEventProducer implements EventProducer {

    private final Producer producer;

    public AliOnsEventProducer(Producer producer) {
        this.producer = producer;
    }

    @Override
    public void publish(String topic, String tag, String message) {
        try {
            /*
             * 底层也是使用的RocketMq, 也是支持多tag模式的
             */
            byte[] body = message.getBytes(StandardCharsets.UTF_8);
            Message msg = new Message(topic, tag, body);
            producer.send(msg);
        } catch (Exception e) {
            throw new MqProduceException("message produce exception:" + e.getMessage(), e);
        }
    }
}
