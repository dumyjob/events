package com.markbolo.event.publisher;

import com.markbolo.event.MessageConverter;
import com.markbolo.event.MqProduceException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

public class RocketPublisher implements EventPublisher {

    private final DefaultMQProducer defaultMQProducer;

    private final MessageConverter messageConverter;

    public RocketPublisher(DefaultMQProducer defaultMQProducer,
                           MessageConverter messageConverter) {
        this.defaultMQProducer = defaultMQProducer;
        this.messageConverter = messageConverter;
    }

    @Override
    public void publish(String topic, String tag, Object message) {
        try {
            // tag需要支持多tag模式
            Message msg = new Message(topic, tag, messageConverter.format(message).getBytes(StandardCharsets.UTF_8));
            defaultMQProducer.send(msg);
        } catch (Exception e ) {
           throw  new MqProduceException("message produce exception:"+ e.getMessage(), e );
        }
    }
}
