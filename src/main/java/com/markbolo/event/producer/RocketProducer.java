package com.markbolo.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.markbolo.event.MessageConverter;
import com.markbolo.event.MqProduceException;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;

public class RocketProducer implements EventProducer {

    private final DefaultMQProducer defaultMQProducer;

    private final MessageConverter messageConverter;

    public RocketProducer(DefaultMQProducer defaultMQProducer,
                          MessageConverter messageConverter) {
        this.defaultMQProducer = defaultMQProducer;
        this.messageConverter = messageConverter;
    }

    @Override
    public void publish(String topic, String tag, Object message) {
        /*
         * RocketMq支持多tag模式
         */
        try {
            byte[] body = messageConverter.format(message).getBytes(StandardCharsets.UTF_8);
            Message msg = new Message(topic, tag, body);
            defaultMQProducer.send(msg);
        } catch (JsonProcessingException | MQClientException | RemotingException | MQBrokerException e) {
            throw new MqProduceException("message produce exception:" + e.getMessage(), e);
        } catch (InterruptedException e) {
            // 使其他线程能够感知此线程已经被中断
            Thread.currentThread().interrupt();
            throw new MqProduceException(e);
        }

    }
}
